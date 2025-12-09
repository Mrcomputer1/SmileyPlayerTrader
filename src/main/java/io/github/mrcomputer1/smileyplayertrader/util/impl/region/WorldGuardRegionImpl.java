package io.github.mrcomputer1.smileyplayertrader.util.impl.region;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.entity.Player;

public class WorldGuardRegionImpl implements IRegionImpl {

    private StateFlag overallFlag;
    private StateFlag rightClickFlag;
    private StateFlag remoteFlag;

    @Override
    public boolean isAllowedOverall(Player player) {
        if (overallFlag == null)
            return true;

        LocalPlayer localPlayer  = WorldGuardPlugin.inst().wrapPlayer(player);

        // Bypass
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        // Region
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(localPlayer.getLocation(), localPlayer, overallFlag);
    }

    @Override
    public boolean isAllowedRightClick(Player player) {
        if (rightClickFlag == null)
            return true;

        LocalPlayer localPlayer  = WorldGuardPlugin.inst().wrapPlayer(player);

        // Bypass
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        // Region
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(localPlayer.getLocation(), localPlayer, rightClickFlag);
    }

    @Override
    public boolean isAllowedRemote(Player player) {
        if (remoteFlag == null)
            return true;

        LocalPlayer localPlayer  = WorldGuardPlugin.inst().wrapPlayer(player);

        // Bypass
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        // Region
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(localPlayer.getLocation(), localPlayer, remoteFlag);
    }

    private StateFlag registerStateFlag(String name) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag(name, true);
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(name);
            if (existing instanceof StateFlag) {
                return (StateFlag) existing;
            } else {
                SmileyPlayerTrader.getInstance().getLogger().warning("Failed to register WorldGuard flag '" + name + "' due to incompatible conflicting flag from another plugin.");
                return null;
            }
        }
    }

    @Override
    public void registerFlags() {
        this.overallFlag = registerStateFlag("smiley-player-trader");
        this.rightClickFlag = registerStateFlag("spt-right-click-trade");
        this.remoteFlag = registerStateFlag("spt-remote-trade");
    }

}
