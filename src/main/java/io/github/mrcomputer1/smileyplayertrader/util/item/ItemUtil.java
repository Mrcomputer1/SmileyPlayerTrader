package io.github.mrcomputer1.smileyplayertrader.util.item;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations.IStockLocation;
import io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations.StockLocations;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemUtil {

    public static int computeAdjustedPrice(MerchantRecipe recipe, int specialPrice){
        return recipe.getIngredients().get(0).getAmount() + specialPrice;
    }

    public static void giveEarnings(OfflinePlayer player, MerchantRecipe recipe, int specialPrice, long productId){
        ItemStack first = recipe.getIngredients().get(0).clone();
        first.setAmount(first.getAmount() + specialPrice);

        ItemStack second = recipe.getIngredients().size() >= 2 ? recipe.getIngredients().get(1).clone() : null;

        List<IStockLocation> stockLocations = StockLocations.getActiveStockLocations();
        for(IStockLocation location : stockLocations){
            if(location.isAvailable(player)){
                if(first != null)
                    first = location.giveEarnings(player, first, productId, true);
                if(second != null)
                    second = location.giveEarnings(player, second, productId, false);
            }

            if(first == null && second == null) // all earnings given
                return;
        }

        if(player.isOnline()){
            Player p = player.getPlayer();
            assert p != null;

            if(first != null)
                p.getWorld().dropItem(p.getLocation(), first);
            if(second != null)
                p.getWorld().dropItem(p.getLocation(), second);
        }
    }

    public static void removeStock(OfflinePlayer player, ItemStack item, long productId){
        item = item.clone();

        List<IStockLocation> stockLocations = StockLocations.getActiveStockLocations();
        for(IStockLocation location : stockLocations){
            if(location.isAvailable(player)){
                item = location.removeStock(player, item, productId);
                if(item == null)
                    return;
            }
        }
    }

    public static boolean doesPlayerHaveItem(OfflinePlayer player, ItemStack item, long productId){
        item = item.clone();

        int found = 0;

        List<IStockLocation> stockLocations = StockLocations.getActiveStockLocations();
        for(IStockLocation location : stockLocations){
            if(location.isAvailable(player)){
                found += location.doesPlayerHaveItem(player, item, productId);
                if(found >= item.getAmount()){
                    return true;
                }else{
                    item.setAmount(item.getAmount() - found);
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

    public static boolean isHiddenItem(List<String> hiddenItems, Material m){
        if (hiddenItems.contains(m.name())) {
            return true;
        }

        if (hiddenItems.contains("%SPAWN_EGGS%") && m.name().endsWith("_SPAWN_EGG")){
            return true;
        }

        return false;
    }

    public static void collectEarnings(Player p){
        int collected = 0;
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.FIND_PRODUCTS_WITH_EARNINGS, p.getUniqueId().toString())) {
            while (set.next()) {
                ItemStack cost = VersionSupport.byteArrayToItemStack(set.getBytes("cost1"));

                byte[] cost2Bytes = set.getBytes("cost2");
                ItemStack cost2 = cost2Bytes == null ? null : VersionSupport.byteArrayToItemStack(cost2Bytes);

                int amount = set.getInt("stored_cost");
                int amount2 = set.getInt("stored_cost2");

                if(amount > 0 || amount2 > 0){
                    collected += 1;
                }

                cost.setAmount(amount);
                Map<Integer, ItemStack> errs = p.getInventory().addItem(cost);
                for(ItemStack is : errs.values()){
                    p.getWorld().dropItem(p.getLocation(), is);
                }

                if(cost2 != null) {
                    cost2.setAmount(amount2);
                    errs = p.getInventory().addItem(cost2);
                    for(ItemStack is : errs.values()){
                        p.getWorld().dropItem(p.getLocation(), is);
                    }
                }

                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_STORED_COST, 0, set.getLong("id"));
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_STORED_COST2, 0, set.getLong("id"));
            }

            if(collected > 0) {
                p.sendMessage(I18N.translate("&aCollected earnings."));
            }else{
                p.sendMessage(I18N.translate("&cYou have no earnings to collect."));
            }
        }catch (SQLException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }

}
