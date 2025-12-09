package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.util.impl.region.IRegionImpl;
import io.github.mrcomputer1.smileyplayertrader.util.impl.region.NullRegionImpl;
import io.github.mrcomputer1.smileyplayertrader.util.impl.region.WorldGuardRegionImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RegionUtil {

    private static IRegionImpl impl;

    public static void setup() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            impl = new WorldGuardRegionImpl();
        } else {
            impl = new NullRegionImpl();
        }

        impl.registerFlags();
    }

    public static boolean isAllowedOverall(Player player) {
        return impl.isAllowedOverall(player);
    }

    public static boolean isAllowedRightClick(Player player) {
        return impl.isAllowedRightClick(player);
    }

    public static boolean isAllowedRemote(Player player) {
        return impl.isAllowedRemote(player);
    }

}
