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

        impl.registerFlag();
    }

    public static boolean isAllowed(Player player) {
        return impl.isAllowed(player);
    }

}
