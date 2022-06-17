package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class InventoryStockLocation implements IStockLocation {

    private Inventory getInventory(OfflinePlayer player){
        if(!player.isOnline())
            throw new IllegalStateException("Player is not online.");

        //noinspection ConstantConditions
        return player.getPlayer().getInventory();
    }

    @Override
    public ItemStack giveEarnings(OfflinePlayer player, ItemStack stack, long id, boolean primaryCost) {
        Inventory inventory = getInventory(player);

        Map<Integer, ItemStack> errs = inventory.addItem(stack);
        if(errs.isEmpty())
            return null;

        int count = 0;
        for(ItemStack is : errs.values()){
            count += is.getAmount();
        }

        ItemStack out = stack.clone();
        out.setAmount(count);
        return out;
    }

    @Override
    public ItemStack removeStock(OfflinePlayer player, ItemStack stack, long id) {
        Inventory inventory = getInventory(player);

        int needed = stack.getAmount();

        for(ItemStack is : inventory.getStorageContents()){
            if(is == null)
                continue;

            if(is.isSimilar(stack)){
                if(is.getAmount() >= needed){
                    is.setAmount(is.getAmount() - needed);;
                    return null;
                }else{
                    needed -= is.getAmount();
                    is.setAmount(0);
                }
            }
        }

        if(needed > 0){
            ItemStack out = stack.clone();
            out.setAmount(needed);
            return out;
        }else return null;
    }

    @Override
    public int doesPlayerHaveItem(OfflinePlayer player, ItemStack stack, long id) {
        Inventory inventory = getInventory(player);
        int found = 0;

        for(ItemStack is : inventory.getStorageContents()){
            if(is == null)
                continue;

            if(is.isSimilar(stack)){
                found += is.getAmount();
                if(found >= stack.getAmount())
                    return found;
            }
        }

        return found;
    }

    @Override
    public boolean isAvailable(OfflinePlayer player) {
        return player.isOnline();
    }

}
