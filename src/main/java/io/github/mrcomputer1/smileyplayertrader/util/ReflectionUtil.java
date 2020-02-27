package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.versions.IMCVersion;
import io.github.mrcomputer1.smileyplayertrader.versions.MCVersion1_15;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {

    public static IMCVersion getVersion(){
        String version = Bukkit.getBukkitVersion().split("-")[0];
        SmileyPlayerTrader.getInstance().getLogger().info("Bukkit version is '" + Bukkit.getBukkitVersion() + "', detected Minecraft version is '" + version + "'.");
        String[] versionParts = version.split("\\.");
        if(versionParts[1].equalsIgnoreCase("15")){ // 1.15, 1.15.1, 1.15.2
            return new MCVersion1_15();
        }
        return null;
    }

    public static ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException {
        return SmileyPlayerTrader.getInstance().getNMS().byteArrayToItemStack(array);
    }

    public static byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException {
        return SmileyPlayerTrader.getInstance().getNMS().itemStackToByteArray(itemStack);
    }

}
