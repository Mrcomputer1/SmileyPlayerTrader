package io.github.mrcomputer1.smileyplayertrader.util.merchant;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MerchantUtil {

    private static final Map<Player, Map<ItemStack, Long>> merchantProductIdCache = new HashMap<>();

    public static long getProductId(Player player, ItemStack stack){
        if(!merchantProductIdCache.containsKey(player))
            return -1;
        if(!merchantProductIdCache.get(player).containsKey(stack))
            return -1;
        return merchantProductIdCache.get(player).get(stack);
    }

    public static void clearProductIdCache(Player player){
        merchantProductIdCache.remove(player);
    }

    public static void openMerchant(Player player, Player store, boolean unsuccessfulFeedback, boolean isReopen){
        if(store == null || !store.isOnline()){
            if(unsuccessfulFeedback)
                player.sendMessage(I18N.translate("&cYou cannot trade with offline players."));
            return;
        }

        if(SmileyPlayerTrader.getInstance().getConfig().getStringList("disabledWorlds").contains(player.getWorld().getName())){
            if(unsuccessfulFeedback)
                player.sendMessage(I18N.translate("&cYou cannot trade in this world."));
            return;
        }

        if(player.getUniqueId().equals(store.getUniqueId()) && !SmileyPlayerTrader.getInstance().getConfig().getBoolean("debugSelfTrading", false)){
            if(unsuccessfulFeedback)
                player.sendMessage(I18N.translate("&cYou cannot trade with yourself."));
            return;
        }

        if(!store.hasPermission("smileyplayertrader.merchant")) {
            if(unsuccessfulFeedback)
                player.sendMessage(I18N.translate("&cThat player cannot be traded with."));
            return;
        }

        Map<ItemStack, Long> productIdCache = new IdentityHashMap<>();

        Merchant merchant = MerchantUtil.buildMerchant(store, productIdCache);
        player.openMerchant(merchant, true);

        if(!isReopen)
            store.sendMessage(I18N.translate("&e%0% is now trading with you.", player.getName()));

        merchantProductIdCache.put(player, productIdCache);
    }

    public static Merchant buildMerchant(Player merchant, Map<ItemStack, Long> productIdCache){
        Merchant m = Bukkit.createMerchant(I18N.translate("&2Villager Store: ") + merchant.getName());

        try {
            VersionSupport.setRecipesOnMerchant(m, getAndBuildRecipes(merchant, productIdCache));
        } catch (InvocationTargetException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to build item list for merchant.");
            e.printStackTrace();
        }

        return m;
    }

    public static ItemStack buildItem(byte[] data){
        try {
            return VersionSupport.byteArrayToItemStack(data);
        } catch (InvocationTargetException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to build item for merchant recipe, skipping...");
            e.printStackTrace();
            return null;
        }
    }

    private static List<MerchantRecipe> getAndBuildRecipes(Player merchant, Map<ItemStack, Long> productIdCache){
        List<MerchantRecipe> recipes = new ArrayList<>();

        int index = 0;
        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.FIND_PRODUCTS, merchant.getUniqueId().toString());
        try {
            while (set.next()) {
                if(!set.getBoolean("enabled"))
                    continue;
                byte[] productb = set.getBytes("product");
                ItemStack is = null;
                if(productb != null) {
                    is = buildItem(productb);
                    if (is == null) {
                        continue;
                    }
                }else{
                    continue;
                }
                MerchantRecipe mr = new MerchantRecipe(is, 0, Integer.MAX_VALUE, true);

                byte[] cost1b = set.getBytes("cost1");
                if(cost1b != null){
                    ItemStack cost1 = buildItem(cost1b);
                    if(cost1 == null){
                        continue;
                    }
                    mr.addIngredient(cost1);
                }else{
                    continue;
                }

                byte[] cost2b = set.getBytes("cost2");
                if(cost2b != null){
                    mr.addIngredient(buildItem(cost2b));
                }

                if(!ItemUtil.doesPlayerHaveItem(merchant, is)){
                    mr.setUses(Integer.MAX_VALUE);
                }

                if(!set.getBoolean("available")){
                    mr.setUses(Integer.MAX_VALUE);
                }

                mr.setSpecialPrice(-set.getInt("special_price"));

                productIdCache.put(mr.getResult(), set.getLong("id"));
                recipes.add(mr);
            }
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to fully load merchant recipes!");
            e.printStackTrace();
        }

        return recipes;
    }

}
