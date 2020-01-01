package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.MerchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

public class EventListener implements Listener {

    @EventHandler
    public void onEntityRightClick(PlayerInteractAtEntityEvent e){
        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("disabledWorlds").contains(e.getPlayer().getWorld().getName())){
            return;
        }
        if(!e.getPlayer().hasPermission("smileyplayertrader.trade"))
            return;

        if(e.getRightClicked().getType() == EntityType.PLAYER){
            Player store = (Player) e.getRightClicked();
            if(!store.hasPermission("smileyplayertrader.merchant"))
                return;
            Merchant merchant = MerchantUtil.buildMerchant(store);
            e.getPlayer().openMerchant(merchant, true);
            store.sendMessage(I18N.translate("&e%0% is now trading with you.", e.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            if(e.getView().getTitle().startsWith(I18N.translate("&2Villager Store: ")) && e.getSlot() == 2){
                MerchantInventory mi = (MerchantInventory)e.getInventory();
                Player store = Bukkit.getPlayer(e.getView().getTitle().replace(I18N.translate("&2Villager Store: "), ""));
                if(mi.getSelectedRecipe() == null){
                    return;
                }
                if(ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())){
                    ItemUtil.removeStock(store, mi.getSelectedRecipe().getResult());
                    ItemUtil.giveEarnings(store, mi.getSelectedRecipe());
                    if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("autoThanks")) {
                        store.chat(I18N.translate("&aThanks for your purchase, %0%", e.getWhoClicked().getName()));
                    }else{
                        e.getWhoClicked().sendMessage(I18N.translate("&aYou purchased an item from %0%", store.getName()));
                    }
                    store.sendMessage(I18N.translate("&a%0% just purchased %1%!", e.getWhoClicked().getName(), mi.getSelectedRecipe().getResult().getType()));
                    if(!ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())){
                        store.sendMessage(I18N.translate("&c%0% is now out of stock!", mi.getSelectedRecipe().getResult().getType()));
                        mi.getSelectedRecipe().setUses(Integer.MAX_VALUE);
                        e.getWhoClicked().openMerchant(mi.getMerchant(), true);
                    }
                }else{
                    e.getWhoClicked().sendMessage(I18N.translate("&cThis item is out of stock!"));
                    e.setCancelled(true);
                }
            }
        }
    }

}
