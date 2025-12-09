package io.github.mrcomputer1.smileyplayertrader.util.impl.region;

import org.bukkit.entity.Player;

public class NullRegionImpl implements IRegionImpl {

    @Override
    public boolean isAllowedOverall(Player player) {
        return true;
    }

    @Override
    public boolean isAllowedRightClick(Player player) {
        return true;
    }

    @Override
    public boolean isAllowedRemote(Player player) {
        return true;
    }

    @Override
    public void registerFlags() {
    }

}
