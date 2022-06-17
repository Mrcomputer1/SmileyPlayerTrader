package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface IStockLocation {

    ItemStack giveEarnings(OfflinePlayer player, ItemStack stack, long id, boolean primaryCost);
    ItemStack removeStock(OfflinePlayer player, ItemStack stack, long id);
    int doesPlayerHaveItem(OfflinePlayer player, ItemStack stack, long id);

    boolean isAvailable(OfflinePlayer player);

}
