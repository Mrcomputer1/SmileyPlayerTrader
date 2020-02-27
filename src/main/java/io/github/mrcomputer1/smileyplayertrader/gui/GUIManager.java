package io.github.mrcomputer1.smileyplayertrader.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager implements Listener  {

    private static GUIManager INSTANCE = new GUIManager();

    public static GUIManager getInstance(){
        return INSTANCE;
    }

    private GUIManager(){}

    private Map<String, AbstractGUI> openGuis = new HashMap<>();
    private List<String> switchingGuis = new ArrayList<>();

    public void openGUI(Player player, AbstractGUI gui){
        if(this.openGuis.containsKey(player.getName())){
            this.openGuis.get(player.getName()).close();
            this.switchingGuis.add(player.getName());
        }

        this.openGuis.put(player.getName(), gui);
        gui.open(player);
        player.openInventory(gui.getInventory());
    }

    public void closeGUI(Player player, boolean force){
        this.openGuis.get(player.getName()).close();
        this.openGuis.remove(player.getName());
        if(force){
            player.closeInventory();
        }
    }

    public boolean isGUIOpen(Player player){
        return openGuis.containsKey(player.getName());
    }

    void onPlayerClick(InventoryClickEvent e){
        if(this.openGuis.containsKey(e.getWhoClicked().getName())) {
            boolean b = this.openGuis.get(e.getWhoClicked().getName()).click(e);
            e.setCancelled(b);
        }
    }

    void onPlayerClose(InventoryCloseEvent e){
        if(this.switchingGuis.contains(e.getPlayer().getName())){
            this.switchingGuis.remove(e.getPlayer().getName());
            return;
        }
        if(this.openGuis.containsKey(e.getPlayer().getName())) {
            this.closeGUI((Player) e.getPlayer(), false);
        }
    }

    void onPlayerQuit(PlayerQuitEvent e){
        if(this.openGuis.containsKey(e.getPlayer().getName())) {
            this.closeGUI(e.getPlayer(), false);
        }
    }

}
