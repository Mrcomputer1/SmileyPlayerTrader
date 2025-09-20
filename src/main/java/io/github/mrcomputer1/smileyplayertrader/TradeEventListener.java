package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations.StockLocations;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeEventListener implements Listener {

    private static final int TRADE_SLOT = 2;

    @EventHandler
    public void onEntityRightClick(PlayerInteractAtEntityEvent e){
        if(e.getHand() != EquipmentSlot.HAND)
            return;

        if(SmileyPlayerTrader.getInstance().getConfiguration().getDisableRightClickTrading())
            return;
        if(e.getRightClicked().hasMetadata("NPC")) // Citizens NPCs seem like players but they have different UUIDs and can't work.
            return;
        if(!e.getPlayer().hasPermission("smileyplayertrader.trade"))
            return;

        PlayerConfig.Config playerConfig = SmileyPlayerTrader.getInstance().getPlayerConfig().getPlayer(e.getPlayer());
        if(playerConfig == null) // If playerConfig is null, assume player is offline.
            return;
        if(!playerConfig.tradeToggle || SmileyPlayerTrader.getInstance().getPlayerConfig().isLocked(e.getPlayer()))
            return;

        if(e.getRightClicked().getType() == EntityType.PLAYER){
            Player store = (Player) e.getRightClicked();
            MerchantUtil.openMerchant(e.getPlayer(), store, false, false);
        }
    }

    private void verifyTradeStillValid(InventoryClickEvent e, MerchantInventory mi, ResultSet set)
            throws SQLException, InvocationTargetException {
        // Check hidden/disabled
        if (!set.getBoolean("enabled") || !set.getBoolean("available")) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product is no longer for sale."));
            return;
        }

        // Check product remains unchanged
        byte[] productBytes = set.getBytes("product");
        if (productBytes == null) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }
        ItemStack product = VersionSupport.byteArrayToItemStack(productBytes);
        if (product == null || !product.equals(mi.getSelectedRecipe().getResult())) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }

        // Check cost 1 remains unchanged
        byte[] cost1Bytes = set.getBytes("cost1");
        if (cost1Bytes == null) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }
        ItemStack cost1 = VersionSupport.byteArrayToItemStack(cost1Bytes);
        ItemStack cost1InSlot = mi.getSelectedRecipe().getIngredients().get(0);
        if (cost1 == null || !cost1.equals(cost1InSlot)) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }

        // Check cost 2 remains unchanged
        byte[] cost2Bytes = set.getBytes("cost2");
        if (cost2Bytes == null && mi.getSelectedRecipe().getIngredients().size() >= 2) { // if ingredients >= 2, cost2 can't have been null
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }
        if (cost2Bytes != null) {
            ItemStack cost2 = VersionSupport.byteArrayToItemStack(cost2Bytes);
            ItemStack cost2InSlot = mi.getSelectedRecipe().getIngredients().size() >= 2 ?
                    mi.getSelectedRecipe().getIngredients().get(1) : null;
            if (cost2 == null || !cost2.equals(cost2InSlot)) {
                e.setCancelled(true);
                GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                return;
            }
        }

        // Check discount remains unchanged
        int discount = -set.getInt("special_price");
        if (discount != VersionSupport.getSpecialCountForRecipe(mi)) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product has been changed, please reopen the trading UI."));
            return;
        }

        // Check purchase count remains within purchase limit
        int purchaseLimit = set.getInt("purchase_limit");
        int purchaseCount = set.getInt("purchase_count");
        if (purchaseLimit != -1 && purchaseCount >= purchaseLimit) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product is no longer for sale."));
            return;
        }
    }

    private void handleItemRanOutOfStockAfterPurchase(InventoryClickEvent e, MerchantInventory mi, OfflinePlayer store, long productId, boolean unlimitedSupply)
            throws InvocationTargetException{
        if (!unlimitedSupply && !ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult(), productId)) {
            // If the store player is online, inform them the item is now out of stock.
            if (store.isOnline())
                store.getPlayer().sendMessage(I18N.translate("&c%0% is now out of stock!", mi.getSelectedRecipe().getResult().getType()));

            // Manually handle the purchase in the trade window.
            e.setCancelled(true);
            ItemStack cost1 = e.getInventory().getItem(0);
            ItemStack cost2 = e.getInventory().getItem(1);
            cost1.setAmount(cost1.getAmount() - ItemUtil.computeAdjustedPrice(mi.getSelectedRecipe(), VersionSupport.getSpecialCountForRecipe(mi)));
            if (mi.getSelectedRecipe().getIngredients().size() >= 2) {
                cost2.setAmount(cost2.getAmount() - mi.getSelectedRecipe().getIngredients().get(1).getAmount());
            }

            final Runnable reopenMerchant = () -> {
                // Reopen the merchant.
                MerchantUtil.openMerchant((Player) e.getWhoClicked(), store, true, true);

                // Restore the cursor item.
                InventoryView iv = e.getWhoClicked().getOpenInventory();
                iv.setCursor(mi.getSelectedRecipe().getResult());
            };

            if (GeyserUtil.isBedrockPlayer((Player) e.getWhoClicked())) {
                // On Bedrock, close the inventory and re-open it a second later.
                e.getWhoClicked().closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), reopenMerchant, 20L);
            } else {
                // On Java, just re-open the inventory without closing the existing one or waiting.
                reopenMerchant.run();
            }
        }
    }

    private void processItemPurchase(InventoryClickEvent e, MerchantInventory mi, OfflinePlayer store, long productId, boolean unlimitedSupply) {
        assert mi.getSelectedRecipe() != null;

        // Remove the item from the store and give them their earnings.
        if (!unlimitedSupply)
            ItemUtil.removeStock(store, mi.getSelectedRecipe().getResult(), productId);

        try {
            if (!unlimitedSupply || SmileyPlayerTrader.getInstance().getConfiguration().getDoesUnlimitedSupplyEarn())
                ItemUtil.giveEarnings(store, mi.getSelectedRecipe(), VersionSupport.getSpecialCountForRecipe(mi), productId);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            SmileyPlayerTrader.getInstance().getLogger().severe("Something went wrong while attempting to give earnings to " + store.getName());
        }

        // Thank the purchaser and increase the purchase count
        MerchantUtil.thankPurchaser(store, (Player) e.getWhoClicked());
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.INCREMENT_PURCHASE_COUNT, productId);

        // If the store player is online, inform them of the purchase.
        if (store.isOnline())
            store.getPlayer().sendMessage(I18N.translate("&a%0% just purchased %1%!", e.getWhoClicked().getName(), mi.getSelectedRecipe().getResult().getType()));

        // Check if the store has run out of stock of that item.
        try {
            handleItemRanOutOfStockAfterPurchase(e, mi, store, productId, unlimitedSupply);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    private void onTradeSlotClick(InventoryClickEvent e) {
        if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
            e.setCancelled(true);
            return;
        }

        MerchantInventory mi = (MerchantInventory) e.getInventory();

        // Get player and ensure player can be traded with.
        //noinspection deprecation
        OfflinePlayer store = Bukkit.getOfflinePlayer(e.getView().getTitle().replace(I18N.translate("&2Villager Store: "), ""));
        if (!store.isOnline() && !StockLocations.canTradeWithPlayer(store)) {
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cYou cannot trade with offline players."));
            return;
        }

        // Ensure a recipe is selected.
        if (mi.getSelectedRecipe() == null)
            return;

        // Check if trade is still valid
        long productId = MerchantUtil.getProductId((Player) e.getWhoClicked(), VersionSupport.getMerchantRecipeOriginalResult(mi.getSelectedRecipe()));
        boolean unlimitedSupply;
        try (ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, productId)) {
            if (set.next()) {
                // Verify the trade is still valid.
                verifyTradeStillValid(e, mi, set);

                // Retrieve unlimited supply
                unlimitedSupply = set.getBoolean("unlimited_supply");
            } else {
                e.setCancelled(true);
                GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis product is no longer for sale."));
                return;
            }
        } catch (SQLException | InvocationTargetException ex) {
            ex.printStackTrace();
            e.setCancelled(true);
            return;
        }

        // Check if the player has the item
        if (unlimitedSupply || ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult(), productId)) {
            // Process purchase.
            processItemPurchase(e, mi, store, productId, unlimitedSupply);
        } else {
            // Item is out of stock.
            e.setCancelled(true);
            GUIManager.sendErrorMessage(e.getWhoClicked(), I18N.translate("&cThis item is out of stock!"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            if(e.getView().getTitle().startsWith(I18N.translate("&2Villager Store: "))){
                if(e.getRawSlot() == TRADE_SLOT) {
                    onTradeSlotClick(e);
                }
            } else if (e.getView().getTitle().startsWith(I18N.translate("&2Preview Store: ")) && e.getRawSlot() == TRADE_SLOT){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            MerchantUtil.clearProductIdCache((Player) e.getPlayer());
        }
    }

}
