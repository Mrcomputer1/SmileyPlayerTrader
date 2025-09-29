package io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public interface IItemIntegrationImpl {

    ItemStack getItem(LinkedHashMap<String, Object> item);

}
