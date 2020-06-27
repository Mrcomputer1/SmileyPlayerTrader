package io.github.mrcomputer1.smileyplayertrader.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGUI {

    private Inventory inventory;

    protected Inventory getInventory(){
        return this.inventory;
    }

    protected void createInventory(String name, int rows){
        this.inventory = Bukkit.createInventory(null, rows * 9, name);
    }

    protected static ItemStack createItem(Material material, int count, String name){
        ItemStack is = new ItemStack(material, count);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return is;
    }

    protected static ItemStack createItemWithLore(Material material, int count, String name, String... lore){
        ItemStack is = new ItemStack(material, count);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        List<String> data = new ArrayList<>(Arrays.asList(lore));
        im.setLore(data);
        is.setItemMeta(im);
        return is;
    }

    public abstract boolean click(InventoryClickEvent e);
    public abstract void close();
    public abstract void open(Player player);

}
