package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ItemUtil {

    public static void giveEarnings(Player player, MerchantRecipe recipe, int specialPrice){
        ItemStack first = recipe.getIngredients().get(0).clone();
        first.setAmount(first.getAmount() + specialPrice);

        if(recipe.getIngredients().size() == 1){
            HashMap<Integer, ItemStack> errs = player.getInventory().addItem(first);
            for(ItemStack v : errs.values()){
                HashMap<Integer, ItemStack> errs2 = player.getEnderChest().addItem(v);
                for(ItemStack v2 : errs2.values()){
                    player.getWorld().dropItem(player.getLocation(), v2);
                }
            }
        }else{
            HashMap<Integer, ItemStack> errs = player.getInventory().addItem(first, recipe.getIngredients().get(1));
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

    public static ItemStack buildConfigurationItem(LinkedHashMap<String, Object> item){
        if(!item.containsKey("type") || !(item.get("type") instanceof String)){
            SmileyPlayerTrader.getInstance().getLogger().severe("type is not String");
            return null;
        }

        ItemStack is;

        // Vanilla handler
        if(!item.containsKey("is") || !(item.get("is") instanceof String) || ((String) item.get("is")).equalsIgnoreCase("vanilla")){
            // Get material
            Material mat = Material.getMaterial((String) item.get("type"));
            if(mat == null){
                if(SmileyPlayerTrader.getInstance().getDescription().getVersion().contains("SNAPSHOT")){
                    SmileyPlayerTrader.getInstance().getLogger().warning(item.get("type") + " does not exist (is it spelled right or from a version this server doesn't support?)");
                }
                return null;
            }

            if(!mat.isItem() || mat.isAir()){
                SmileyPlayerTrader.getInstance().getLogger().severe("Material is not an item/block or is air.");
                return null;
            }

            // Create stack
            is = new ItemStack(mat);
        }else {
            // Other/integration handlers
            String itemIs = (String) item.get("is");

            // Invalid integration
            SmileyPlayerTrader.getInstance().getLogger().severe("Bad item type.");
            return null;
        }

        // Get meta
        if(item.containsKey("meta")){
            Object obj = item.get("meta");
            if(!(obj instanceof ItemMeta)){
                SmileyPlayerTrader.getInstance().getLogger().severe("meta is not ItemMeta");
            }
            ItemMeta im = (ItemMeta) obj;

            // Process display name
            if(im.hasDisplayName())
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', im.getDisplayName()));

            // Process lore
            if(im.hasLore()){
                List<String> lore = im.getLore();
                for(int i = 0; i < lore.size(); i++){
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                }
                im.setLore(lore);
            }

            is.setItemMeta(im);
        }

        return is;
    }

}
