package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SmileyPlayerTrader extends JavaPlugin {

    private static SmileyPlayerTrader INSTANCE;

    public static SmileyPlayerTrader getInstance(){
        return INSTANCE;
    }

    private DatabaseUtil db;

    @Override
    public void onEnable() {
        SmileyPlayerTrader.INSTANCE = this;

        saveDefaultConfig();

        this.db = new DatabaseUtil(new File(getDataFolder(), "database.db"));
        this.db.run("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY," +
                "merchant TEXT NOT NULL," +
                "cost1 BLOB," +
                "cost2 BLOB," +
                "product BLOB," +
                "enabled BOOLEAN DEFAULT 0 NOT NULL)");

        getCommand("smileyplayertrader").setExecutor(new CommandSmileyPlayerTrader());

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        this.db.close();
    }

    public DatabaseUtil getDatabase(){
        return this.db;
    }
}
