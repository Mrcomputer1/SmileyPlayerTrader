package io.github.mrcomputer1.smileyplayertrader;

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
            store.sendMessage(ChatColor.YELLOW + e.getPlayer().getName() + " is now trading with you.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getView().getType() == InventoryType.MERCHANT){
            if(e.getView().getTitle().startsWith(ChatColor.DARK_GREEN + "Villager Store: ") && e.getSlot() == 2){
                MerchantInventory mi = (MerchantInventory)e.getInventory();
                Player store = Bukkit.getPlayer(e.getView().getTitle().replace(ChatColor.DARK_GREEN + "Villager Store: ", ""));
                if(mi.getSelectedRecipe() == null){
                    return;
                }
                if(ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())){
                    ItemUtil.removeStock(store, mi.getSelectedRecipe().getResult());
                    ItemUtil.giveEarnings(store, mi.getSelectedRecipe());
                    if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("autoThanks")) {
                        store.chat(ChatColor.GREEN + "Thanks for your purchase, " + e.getWhoClicked().getName());
                    }else{
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "You purchased an item from " + store.getName());
                    }
                    store.sendMessage(ChatColor.GREEN + e.getWhoClicked().getName() + " just purchased " + mi.getSelectedRecipe().getResult().getType() + "!");
                    if(!ItemUtil.doesPlayerHaveItem(store, mi.getSelectedRecipe().getResult())){
                        store.sendMessage(ChatColor.RED + mi.getSelectedRecipe().getResult().getType().toString() + " is now out of stock!");
                        mi.getSelectedRecipe().setUses(Integer.MAX_VALUE);
                        e.getWhoClicked().openMerchant(mi.getMerchant(), true);
                    }
                }else{
                    e.getWhoClicked().sendMessage(ChatColor.RED + "This item is out of stock!");
                    e.setCancelled(true);
                }
            }
        }
    }

}
