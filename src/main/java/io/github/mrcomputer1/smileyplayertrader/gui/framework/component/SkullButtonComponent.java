package io.github.mrcomputer1.smileyplayertrader.gui.framework.component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullButtonComponent extends ButtonComponent{

    private final String playerName;

    public SkullButtonComponent(int x, int y, Material material, int count, String playerName, String name, String... lore) {
        super(x, y, material, count, name, lore);

        this.playerName = playerName;
    }

    @Override
    protected void makeItemMeta(ItemMeta meta) {
        super.makeItemMeta(meta);

        SkullMeta sm = (SkullMeta) meta;
        //noinspection deprecation
        sm.setOwningPlayer(Bukkit.getOfflinePlayer(this.playerName));
    }

}
