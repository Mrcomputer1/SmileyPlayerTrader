package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.DatabaseUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SmileyPlayerTrader extends JavaPlugin {

    public static SmileyPlayerTrader getInstance(){
        return getPlugin(SmileyPlayerTrader.class);
    }

    private DatabaseUtil db;
    private I18N i18n;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.i18n = new I18N();
        this.i18n.createLanguages();
        this.i18n.loadLanguages();

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

    public I18N getI18N(){
        return this.i18n;
    }
}
