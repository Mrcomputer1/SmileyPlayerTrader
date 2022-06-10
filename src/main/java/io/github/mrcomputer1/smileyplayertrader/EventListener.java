package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class EventListener implements Listener {

    @EventHandler
    public void onEntityRightClick(PlayerInteractAtEntityEvent e){
        if(e.getHand() != EquipmentSlot.HAND)
            return;

        if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("disableRightClickTrading", false))
            return;
        if(e.getRightClicked().hasMetadata("NPC")) // Citizens NPCs seem like players but they have different UUIDs and can't work.
            return;
        if(!e.getPlayer().hasPermission("smileyplayertrader.trade"))
            return;
        if(!SmileyPlayerTrader.getInstance().getPlayerConfig().getPlayer(e.getPlayer()).tradeToggle || SmileyPlayerTrader.getInstance().getPlayerConfig().isLocked(e.getPlayer()))
            return;

        if(e.getRightClicked().getType() == EntityType.PLAYER){
            Player store = (Player) e.getRightClicked();
            MerchantUtil.openMerchant(e.getPlayer(), store, false, false);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            if(e.getView().getTitle().startsWith(I18N.translate("&2Villager Store: ")) && e.getRawSlot() == 2){
                if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
                    e.setCancelled(true);
                    return;
                }
                MerchantInventory mi = (MerchantInventory)e.getInventory();
                Player store = Bukkit.getPlayer(e.getView().getTitle().replace(I18N.translate("&2Villager Store: "), ""));
                if(store == null || !store.isOnline()){
                    e.getWhoClicked().sendMessage(I18N.translate("&cYou cannot trade with offline players."));
                    e.setCancelled(true);
                    return;
                }
                if(mi.getSelectedRecipe() == null){
                    return;
                }

                // Check if trade is still valid
                long productId = MerchantUtil.getProductId((Player) e.getWhoClicked(), mi.getSelectedRecipe().getResult());
                ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, productId);
                try {
                    if (set.next()) {
                        // Check hidden/disabled
                        if(!set.getBoolean("enabled") || !set.getBoolean("available")){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product is no longer for sale."));
                            e.setCancelled(true);
                            return;
                        }

                        // Check product remains unchanged
                        byte[] productBytes = set.getBytes("product");
                        if(productBytes == null){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }
                        ItemStack product = VersionSupport.byteArrayToItemStack(productBytes);
                        if(product == null || !product.equals(mi.getSelectedRecipe().getResult())){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }

                        // Check cost 1 remains unchanged
                        byte[] cost1Bytes = set.getBytes("cost1");
                        if(cost1Bytes == null){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }
                        ItemStack cost1 = VersionSupport.byteArrayToItemStack(cost1Bytes);
                        if(cost1 == null || !cost1.equals(mi.getSelectedRecipe().getIngredients().get(0))){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }

                        // Check cost 2 remains unchanged
                        byte[] cost2Bytes = set.getBytes("cost2");
                        if(cost2Bytes == null && mi.getSelectedRecipe().getIngredients().size() >= 2){ // if ingredients >= 2, cost2 can't have been null
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }
                        if(cost2Bytes != null){
                            ItemStack cost2 = VersionSupport.byteArrayToItemStack(cost2Bytes);
                            if(cost2 == null || !cost2.equals(mi.getSelectedRecipe().getIngredients().get(1))){
                                e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                                e.setCancelled(true);
                                return;
                            }
                        }

                        int discount = -set.getInt("special_price");
                        if(discount != VersionSupport.getSpecialCountForRecipe(mi)){
                            e.getWhoClicked().sendMessage(I18N.translate("&cThis product has been changed, please reopen the trading UI."));
                            e.setCancelled(true);
                            return;
                        }

                    } else {
                        e.getWhoClicked().sendMessage(I18N.translate("&cThis product is no longer for sale."));
                        e.setCancelled(true);
                        return;
                    }
                } catch (SQLException | InvocationTargetException ex) {
                    ex.printStackTrace();
                    e.setCancelled(true);
                    return;
                }

                if(ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())){
                    ItemUtil.removeStock(store, mi.getSelectedRecipe().getResult());
                    try {
                        ItemUtil.giveEarnings(store, mi.getSelectedRecipe(), VersionSupport.getSpecialCountForRecipe(mi));
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                        SmileyPlayerTrader.getInstance().getLogger().severe("Something went wrong while attempting to give earnings to " + store.getName());
                    }
                    if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("autoThanks", true)) {
                        store.chat(I18N.translate("&aThanks for your purchase, %0%", e.getWhoClicked().getName()));
                    }else{
                        e.getWhoClicked().sendMessage(I18N.translate("&aYou purchased an item from %0%", store.getName()));
                    }
                    store.sendMessage(I18N.translate("&a%0% just purchased %1%!", e.getWhoClicked().getName(), mi.getSelectedRecipe().getResult().getType()));
                    try {
                        if (!ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())) {
                            store.sendMessage(I18N.translate("&c%0% is now out of stock!", mi.getSelectedRecipe().getResult().getType()));
                            ItemStack cost1 = e.getInventory().getItem(0);
                            ItemStack cost2 = e.getInventory().getItem(1);
                            cost1.setAmount(cost1.getAmount() - ItemUtil.computeAdjustedPrice(mi.getSelectedRecipe(), VersionSupport.getSpecialCountForRecipe(mi)));
                            if (mi.getSelectedRecipe().getIngredients().size() >= 2) {
                                cost2.setAmount(cost2.getAmount() - mi.getSelectedRecipe().getIngredients().get(1).getAmount());
                            }
                            MerchantUtil.openMerchant((Player) e.getWhoClicked(), store, true, true);
                            InventoryView iv = e.getWhoClicked().getOpenInventory();
                            iv.setCursor(mi.getSelectedRecipe().getResult());
                        }
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    e.getWhoClicked().sendMessage(I18N.translate("&cThis item is out of stock!"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            MerchantUtil.clearProductIdCache((Player) e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(e.getPlayer().hasPermission("smileyplayertrader.admin")){
            if(SmileyPlayerTrader.getInstance().getUpdateChecker() != null) {
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().unsupported) {
                    e.getPlayer().sendMessage(I18N.translate("&c[Smiley Player Trader] This Minecraft version is no longer supported and therefore no support will be given for this version."));
                }
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().failed) {
                    e.getPlayer().sendMessage(I18N.translate("&e[Smiley Player Trader] Failed to check plugin version!"));
                }
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().isOutdated) {
                    e.getPlayer().sendMessage(I18N.translate("&e[Smiley Player Trader] Plugin is outdated! Latest version is %0%. It is recommended to download the update.", SmileyPlayerTrader.getInstance().getUpdateChecker().upToDateVersion));
                }
            }
        }

        SmileyPlayerTrader.getInstance().getPlayerConfig().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        MerchantUtil.clearProductIdCache(e.getPlayer());
        SmileyPlayerTrader.getInstance().getPlayerConfig().unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent e) {
        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("disabledWorlds").contains(e.getEntity().getWorld().getName())){
            return;
        }

        if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("autoCombatLock.enabled", true)) {
            if (e.getDamager() instanceof Player) {
                SmileyPlayerTrader.getInstance().getPlayerConfig().lockPlayer((Player) e.getDamager());
            }
            if (e.getEntity() instanceof Player){
                SmileyPlayerTrader.getInstance().getPlayerConfig().lockPlayer((Player) e.getEntity());
            }
        }
    }

}
