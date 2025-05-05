package io.github.mrcomputer1.smileyplayertrader.util.impl.region;

import org.bukkit.entity.Player;

public interface IRegionImpl {

    boolean isAllowed(Player player);
    void registerFlag();

}
