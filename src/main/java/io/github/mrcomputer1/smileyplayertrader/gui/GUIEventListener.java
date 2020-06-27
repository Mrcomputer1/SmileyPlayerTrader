package io.github.mrcomputer1.smileyplayertrader.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class GUIEventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        GUIManager.getInstance().onPlayerClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        GUIManager.getInstance().onPlayerClose(e);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        GUIManager.getInstance().onPlayerQuit(e);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e){
        if(e.getEntity() instanceof Player){
            if(GUIManager.getInstance().isGUIOpen((Player)e.getEntity())){
                e.setCancelled(true);
            }
        }
    }

}
