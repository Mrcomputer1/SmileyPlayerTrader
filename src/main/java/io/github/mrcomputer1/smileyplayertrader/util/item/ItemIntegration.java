package io.github.mrcomputer1.smileyplayertrader.util.item;

import io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration.IItemIntegrationImpl;
import io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration.NexoImpl;
import io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration.ValhallaMMOImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ItemIntegration {

    private static final Map<String, IItemIntegrationImpl> integrations = new HashMap<>();

    public static void registerItemIntegration(String id, IItemIntegrationImpl impl) {
        integrations.put(id, impl);
    }

    public static boolean hasItemIntegration(String id) {
        return integrations.containsKey(id);
    }

    public static IItemIntegrationImpl getItemIntegration(String id) {
        return integrations.get(id);
    }

    public static boolean isPluginAvailable(String id) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(id);
        return plugin != null && plugin.isEnabled();
    }

    public static void addBuiltinItemIntegrations() {
        registerItemIntegration("nexo", isPluginAvailable("Nexo") ? new NexoImpl() : null);
        registerItemIntegration("valhallammo", isPluginAvailable("ValhallaMMO") ? new ValhallaMMOImpl() : null);
    }

}
