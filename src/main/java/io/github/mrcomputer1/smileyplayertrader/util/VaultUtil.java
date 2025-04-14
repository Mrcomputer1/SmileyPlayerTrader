package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {

    private static boolean getDefaultPermission(String permission) {
        org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(permission);
        if (perm == null)
            return false;
        return perm.getDefault().getValue(false);
    }

    public static boolean hasPermission(World world, OfflinePlayer player, String permission) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
            return getDefaultPermission(permission);
        if (SmileyPlayerTrader.getInstance().getConfiguration().getDisableVaultOfflinePermissionChecking())
            return getDefaultPermission(permission);

        RegisteredServiceProvider<Permission> rsp =
                Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return getDefaultPermission(permission);
        }

        Permission provider = rsp.getProvider();
        return provider.playerHas(world.getName(), player, permission);
    }

}
