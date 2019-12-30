package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;

public class ItemUtil {

    public static void giveEarnings(Player player, MerchantRecipe recipe){
        if(recipe.getIngredients().size() == 1){
            HashMap<Integer, ItemStack> errs = player.getInventory().addItem(recipe.getIngredients().get(0));
            for(ItemStack v : errs.values()){
                HashMap<Integer, ItemStack> errs2 = player.getEnderChest().addItem(v);
                for(ItemStack v2 : errs2.values()){
                    player.getWorld().dropItem(player.getLocation(), v2);
                }
            }
        }else{
            HashMap<Integer, ItemStack> errs = player.getInventory().addItem(recipe.getIngredients().get(0), recipe.getIngredients().get(1));
            for(ItemStack v : errs.values()){
                HashMap<Integer, ItemStack> errs2 = player.getEnderChest().addItem(v);
                for(ItemStack v2 : errs2.values()){
                    player.getWorld().dropItem(player.getLocation(), v2);
                }
            }
        }
    }

    public static void removeStock(Player player, ItemStack item){
        int needed = item.getAmount();

        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("stockLocations").contains("inventory")){
            for(ItemStack is : player.getInventory().getStorageContents()){
                if(is == null)
                    continue;
                if(is.isSimilar(item)){
                    if(is.getAmount() >= needed){
                        is.setAmount(is.getAmount() - needed);
                        return;
                    }else{
                        needed -= is.getAmount();
                        is.setAmount(0);
                    }
                }
            }
        }

        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("stockLocations").contains("enderchest")){
            for(ItemStack is : player.getEnderChest().getStorageContents()){
                if(is == null)
                    continue;
                if(is.isSimilar(item)){
                    if(is.getAmount() >= needed){
                        is.setAmount(is.getAmount() - needed);
                        return;
                    }else{
                        needed -= is.getAmount();
                        is.setAmount(0);
                    }
                }
            }
        }

    }

    public static boolean doesPlayerHaveItem(Player player, ItemStack item){

        int found = 0;

        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("stockLocations").contains("inventory")){
            for(ItemStack is : player.getInventory().getStorageContents()){
                if(is == null)
                    continue;
                if(is.isSimilar(item)){
                    found += is.getAmount();
                    if(found >= item.getAmount()){
                        return true;
                    }
                }
            }
        }

        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("stockLocations").contains("enderchest")){
            for(ItemStack is : player.getEnderChest().getStorageContents()){
                if(is == null)
                    continue;
                if(is.isSimilar(item)){
                    found += is.getAmount();
                    if(found >= item.getAmount()){
                        return true;
                    }
                }
            }
        }

        return false;

    }

}
