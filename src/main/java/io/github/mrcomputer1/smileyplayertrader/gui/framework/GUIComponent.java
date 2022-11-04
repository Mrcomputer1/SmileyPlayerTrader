package io.github.mrcomputer1.smileyplayertrader.gui.framework;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GUIComponent {

    protected final int x, y, width, height;

    public GUIComponent(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void render(Inventory inventory);

    public void onParentClose(Player player){
    }

    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack){
        return false;
    }

}
