package io.github.mrcomputer1.smileyplayertrader.util.merchant;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.VaultUtil;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations.IStockLocation;
import io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations.StockLocations;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public static void openPreviewMerchant(Player player){
        Merchant merchant = MerchantUtil.buildMerchant(player, new IdentityHashMap<>(), true);
        player.openMerchant(merchant, true);
    }

    public static void openMerchant(Player player, OfflinePlayer store, boolean unsuccessfulFeedback, boolean isReopen){
        if(!store.isOnline() && !StockLocations.canTradeWithPlayer(store)){
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

        if(store.isOnline()) {
            //noinspection ConstantConditions
            if (!store.getPlayer().hasPermission("smileyplayertrader.merchant")) {
                if (unsuccessfulFeedback)
                    player.sendMessage(I18N.translate("&cThat player cannot be traded with."));
            }else{
                actuallyOpenMerchant(player, store, unsuccessfulFeedback, isReopen);
            }
        }else{
            Bukkit.getScheduler().runTaskAsynchronously(
                    SmileyPlayerTrader.getInstance(),
                    (task) -> {
                        if(!VaultUtil.hasPermission(player.getWorld(), store, "smileyplayertrader.merchant")){
                            if (unsuccessfulFeedback) {
                                Bukkit.getScheduler().runTask(
                                        SmileyPlayerTrader.getInstance(),
                                        (task2) -> player.sendMessage(I18N.translate("&cThat player cannot be traded with."))
                                );
                            }
                        }else{
                            Bukkit.getScheduler().runTask(SmileyPlayerTrader.getInstance(),
                                    (task2) -> actuallyOpenMerchant(player, store, unsuccessfulFeedback, isReopen));
                        }
                    }
            );
        }


    }

    private static void actuallyOpenMerchant(Player player, OfflinePlayer store, boolean unsuccessfulFeedback, boolean isReopen){
        Map<ItemStack, Long> productIdCache = new IdentityHashMap<>();

        Merchant merchant = MerchantUtil.buildMerchant(store, productIdCache, false);
        player.openMerchant(merchant, true);

        if(!isReopen && store.isOnline())
            //noinspection ConstantConditions
            store.getPlayer().sendMessage(I18N.translate("&e%0% is now trading with you.", player.getName()));

        merchantProductIdCache.put(player, productIdCache);
    }

    public static Merchant buildMerchant(OfflinePlayer merchant, Map<ItemStack, Long> productIdCache, boolean preview){
        Merchant m = Bukkit.createMerchant(
                preview ? I18N.translate("&2Preview Store: ") + merchant.getName()
                        : I18N.translate("&2Villager Store: ") + merchant.getName()
        );

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

    private static List<MerchantRecipe> getAndBuildRecipes(OfflinePlayer merchant, Map<ItemStack, Long> productIdCache){
        List<MerchantRecipe> recipes = new ArrayList<>();

        int index = 0;
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.FIND_PRODUCTS, merchant.getUniqueId().toString())) {
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

                if(!ItemUtil.doesPlayerHaveItem(merchant, is, set.getLong("id"))){
                    String outOfStockBehaviour = SmileyPlayerTrader.getInstance().getConfig().getString("outOfStockBehaviour", "showByDefault");
                    if(outOfStockBehaviour.equalsIgnoreCase("hide"))
                        continue;
                    if(!outOfStockBehaviour.equalsIgnoreCase("show") && set.getBoolean("hide_on_out_of_stock"))
                        continue;
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
