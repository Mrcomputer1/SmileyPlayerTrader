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

    private StateFlag flag;

    @Override
    public boolean isAllowed(Player player) {
        LocalPlayer localPlayer  = WorldGuardPlugin.inst().wrapPlayer(player);

        // Bypass
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        // Region
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(localPlayer.getLocation(), localPlayer, flag);
    }

    @Override
    public void registerFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("smiley-player-trader", true);
            registry.register(flag);
            this.flag = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("smiley-player-trader");
            if (existing instanceof StateFlag) {
                this.flag = (StateFlag) existing;
            } else {
                SmileyPlayerTrader.getInstance().getLogger().warning("Failed to register WorldGuard flag due to incompatible conflicting flag from another plugin.");
            }
        }
    }

}
