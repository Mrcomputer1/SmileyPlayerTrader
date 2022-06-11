package io.github.mrcomputer1.smileyplayertrader.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProductGUIState {

    private static final ItemStack TEMPLATE_ITEM = new ItemStack(Material.AIR);

    // Current product meta
    public final long id;
    public final boolean isEditing;

    // Product items
    public ItemStack stack = TEMPLATE_ITEM.clone();
    public ItemStack costStack = TEMPLATE_ITEM.clone();
    public ItemStack costStack2 = TEMPLATE_ITEM.clone();

    // Product properties
    public int discount = 0;
    public int priority = 0;

    // Menu screen memory
    public final int page;

    public ProductGUIState(int page, long id, ItemStack stack, ItemStack costStack, ItemStack costStack2, int discount, int priority){
        this.page = page;

        this.isEditing = true;
        this.id = id;

        this.stack = stack;
        this.costStack = costStack;
        this.costStack2 = costStack2;

        this.discount = discount;
        this.priority = priority;
    }

    public ProductGUIState(int page){
        this.page = page;

        this.id = -1L;
        this.isEditing = false;
    }

}
