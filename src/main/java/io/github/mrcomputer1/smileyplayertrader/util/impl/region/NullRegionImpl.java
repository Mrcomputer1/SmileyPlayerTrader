package io.github.mrcomputer1.smileyplayertrader.util.impl.region;

import org.bukkit.entity.Player;

public class NullRegionImpl implements IRegionImpl {

    @Override
    public boolean isAllowed(Player player) {
        return true;
    }

    @Override
    public void registerFlag() {
    }

}
