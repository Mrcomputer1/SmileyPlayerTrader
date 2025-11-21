package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ShulkerBoxStockLocation implements IStockLocation {

    private boolean enderChest;

    public ShulkerBoxStockLocation(boolean enderChest) {
        this.enderChest = enderChest;
    }

    private Inventory getInventory(OfflinePlayer player) {
        assert player.getPlayer() != null;

        return enderChest ? player.getPlayer().getEnderChest() : player.getPlayer().getInventory();
    }

    @Override
    public ItemStack giveEarnings(OfflinePlayer player, ItemStack stack, long id, boolean primaryCost) {
        // Don't attempt to store earnings in shulkers.
        return stack;
    }

    @Override
    public ItemStack removeStock(OfflinePlayer player, ItemStack stack, long id) {
        Inventory inventory = getInventory(player);
        int needed = stack.getAmount();

        for (ItemStack is : inventory.getStorageContents()){
            if (is == null || is.getType() != Material.SHULKER_BOX)
                continue;

            // Start get shulker inventory
            ItemMeta im = is.getItemMeta();
            if (!(im instanceof BlockStateMeta))
                continue;

            BlockStateMeta blockStateMeta = (BlockStateMeta) im;
            BlockState blockState = blockStateMeta.getBlockState();
            if (!(blockState instanceof ShulkerBox))
                continue;

            ShulkerBox shulkerBox = (ShulkerBox) blockState;
            Inventory shulkerInventory = shulkerBox.getInventory();;
            // End get shulker inventory

            for (ItemStack item : shulkerInventory.getStorageContents()) {
                if (item == null)
                    continue;

                if (item.isSimilar(stack)) {
                    if (item.getAmount() >= needed) {
                        item.setAmount(item.getAmount() - needed);

                        // Update shulker box
                        shulkerBox.update();
                        blockStateMeta.setBlockState(shulkerBox);
                        is.setItemMeta(blockStateMeta);

                        return null;
                    } else {
                        needed -= item.getAmount();
                        item.setAmount(0);
                    }
                }
            }

            boolean storeInventory = true;
            if (SmileyPlayerTrader.getInstance().getConfiguration().isUseOnlyOneStockLocation()) {
                if (needed > 0) {
                    needed = stack.getAmount();
                    storeInventory = false;
                }
            }

            // Start store shulker inventory
            if (storeInventory) {
                shulkerBox.update();
                blockStateMeta.setBlockState(shulkerBox);
                is.setItemMeta(blockStateMeta);
            }
            // End store shulker inventory
        }

        if (needed > 0){
            ItemStack out = stack.clone();
            out.setAmount(needed);
            return out;
        } else return null;
    }

    @Override
    public int doesPlayerHaveItem(OfflinePlayer player, ItemStack stack, long id) {
        Inventory inventory = getInventory(player);
        int found = 0;

        for (ItemStack is : inventory.getStorageContents()){
            if (is == null || is.getType() != Material.SHULKER_BOX)
                continue;

            // Start get shulker inventory
            ItemMeta im = is.getItemMeta();
            if (!(im instanceof BlockStateMeta))
                continue;

            BlockStateMeta blockStateMeta = (BlockStateMeta) im;
            BlockState blockState = blockStateMeta.getBlockState();
            if (!(blockState instanceof ShulkerBox))
                continue;

            ShulkerBox shulkerBox = (ShulkerBox) blockState;
            Inventory shulkerInventory = shulkerBox.getInventory();;
            // End get shulker inventory

            for (ItemStack item : shulkerInventory.getStorageContents()) {
                if (item == null)
                    continue;

                if (item.isSimilar(stack)) {
                    found += item.getAmount();
                    if (found >= stack.getAmount())
                        return found;
                }
            }

            if (SmileyPlayerTrader.getInstance().getConfiguration().isUseOnlyOneStockLocation())
                found = 0;
        }

        return found;
    }

    @Override
    public boolean isAvailable(OfflinePlayer player) {
        return player.isOnline();
    }

}
