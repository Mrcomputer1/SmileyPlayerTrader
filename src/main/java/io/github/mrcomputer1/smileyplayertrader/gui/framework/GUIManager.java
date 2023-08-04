package io.github.mrcomputer1.smileyplayertrader.gui.framework;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.IdentityHashMap;
import java.util.Map;

public class GUIManager implements Listener {

    private final Map<Inventory, GUI> openGuis = new IdentityHashMap<>();

    public static GUIManager getInstance(){
        return SmileyPlayerTrader.getInstance().getGuiManager();
    }

    public static void sendErrorMessage(HumanEntity human, String message){
        Player player = (Player) human;

        if(GeyserUtil.isBedrockPlayer(player)){
            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () -> {
                GeyserUtil.showSimpleForm(player, I18N.translate("Something went wrong!"), message);
            }, 20L);
        }else{
            player.sendMessage(message);
        }
    }

    public void openGui(Player player, GUI gui){
        GUI currentGui = getGuiFor(player.getOpenInventory().getTopInventory());
        if(currentGui != null){
            this.onInventoryClose(player.getOpenInventory().getTopInventory());
        }

        Inventory inventory = Bukkit.createInventory(null, gui.getRows() * 9, gui.getTitle());
        gui.init(inventory, player);
        this.openGuis.put(inventory, gui);
        player.openInventory(inventory);
    }

    public GUI getGuiFor(Inventory inventory){
        return this.openGuis.get(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        GUI gui = this.getGuiFor(e.getView().getTopInventory());
        if(gui != null)
            gui.onInventoryClick(e);
    }

    private void onInventoryClose(Inventory inventory){
        GUI gui = this.getGuiFor(inventory);
        if(gui != null) {
            gui.onInventoryClose(inventory);
            this.openGuis.remove(inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        this.onInventoryClose(e.getView().getTopInventory());
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(getGuiFor(p.getOpenInventory().getTopInventory()) != null){
                e.setCancelled(true);
            }
        }
    }

}
