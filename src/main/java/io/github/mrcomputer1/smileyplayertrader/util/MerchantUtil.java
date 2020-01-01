package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MerchantUtil {

    public static Merchant buildMerchant(Player merchant){
        Merchant m = Bukkit.createMerchant(I18N.translate("&2Villager Store: ") + merchant.getName());

        m.setRecipes(getAndBuildRecipes(merchant));

        return m;
    }

    public static ItemStack buildItem(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            net.minecraft.server.v1_15_R1.ItemStack nmsIS = net.minecraft.server.v1_15_R1.ItemStack.a(NBTCompressedStreamTools.a(bais));
            return CraftItemStack.asCraftMirror(nmsIS);
        } catch (IOException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to build item for merchant recipe, skipping...");
            e.printStackTrace();
            return null;
        }
    }

    private static List<MerchantRecipe> getAndBuildRecipes(Player merchant){
        List<MerchantRecipe> recipes = new ArrayList<>();

        ResultSet set = SmileyPlayerTrader.getInstance().getDatabase().get("SELECT * FROM products WHERE merchant=?", merchant.getUniqueId().toString());
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
                    mr.addIngredient(buildItem(cost1b));
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

                recipes.add(mr);
            }
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to fully load merchant recipes!");
            e.printStackTrace();
        }

        return recipes;
    }

}
