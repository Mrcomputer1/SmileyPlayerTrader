package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import io.github.mrcomputer1.smileyplayertrader.versions.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ReflectionUtil {

    public static IMCVersion getVersion(){
        String version = Bukkit.getBukkitVersion().split("-")[0];
        SmileyPlayerTrader.getInstance().getLogger().info("Bukkit version is '" + Bukkit.getBukkitVersion() + "', detected Minecraft version is '" + version + "'.");
        String[] versionParts = version.split("\\.");
        if(versionParts[1].equalsIgnoreCase("15")){ // 1.15, 1.15.1, 1.15.2
            return new MCVersion1_15();
        }else if(versionParts[1].equalsIgnoreCase("16") && (versionParts.length < 3 || versionParts[2].equalsIgnoreCase("1"))){ // 1.16.1
            return new MCVersion1_16();
        }else if(versionParts[1].equalsIgnoreCase("16") && (versionParts[2].equalsIgnoreCase("2") || versionParts[2].equalsIgnoreCase("3"))){ // 1.16.2, 1.16.3
            return new MCVersion1_16_R2();
        }else if(versionParts[1].equalsIgnoreCase("16") && (versionParts[2].equalsIgnoreCase("4") || versionParts[2].equalsIgnoreCase("5"))){ // 1.16.4, 1.16.5
            return new MCVersion1_16_R3();
        }else if(versionParts[1].equalsIgnoreCase("17")){ // 1.17, 1.17.1
            return new MCVersion1_17();
        }else if(versionParts[1].equalsIgnoreCase("18")){ // 1.18
            return new MCVersion1_18();
        }
        return null;
    }

    public static ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException {
        return SmileyPlayerTrader.getInstance().getNMS().byteArrayToItemStack(array);
    }

    public static byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException {
        return SmileyPlayerTrader.getInstance().getNMS().itemStackToByteArray(itemStack);
    }

    public static void setRecipesOnMerchant(Merchant merchant, List<MerchantRecipe> recipes) throws InvocationTargetException {
        SmileyPlayerTrader.getInstance().getNMS().setRecipesOnMerchant(merchant, recipes);
    }

}
