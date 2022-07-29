package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class NoOpStockLocation implements IStockLocation{
    @Override
    public ItemStack giveEarnings(OfflinePlayer player, ItemStack stack, long id, boolean primaryCost) {
        return stack;
    }

    @Override
    public ItemStack removeStock(OfflinePlayer player, ItemStack stack, long id) {
        return stack;
    }

    @Override
    public int doesPlayerHaveItem(OfflinePlayer player, ItemStack stack, long id) {
        return 0;
    }

    @Override
    public boolean isAvailable(OfflinePlayer player) {
        return true;
    }
}
