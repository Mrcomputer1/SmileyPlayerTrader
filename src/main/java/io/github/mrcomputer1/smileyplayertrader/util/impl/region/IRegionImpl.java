package io.github.mrcomputer1.smileyplayertrader.util.impl.region;

import org.bukkit.entity.Player;

public interface IRegionImpl {

    boolean isAllowedOverall(Player player);
    boolean isAllowedRightClick(Player player);
    boolean isAllowedRemote(Player player);
    void registerFlags();

}
