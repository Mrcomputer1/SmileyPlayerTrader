package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.versions.IMCVersion;
import io.github.mrcomputer1.smileyplayertrader.util.DatabaseUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ReflectionUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SmileyPlayerTrader extends JavaPlugin {

    public static SmileyPlayerTrader getInstance(){
        return getPlugin(SmileyPlayerTrader.class);
    }

    private DatabaseUtil db;
    private I18N i18n;
    private UpdateChecker updateChecker = null;
    private IMCVersion nms = null;

    private Metrics metrics;

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this);

        saveDefaultConfig();

        this.i18n = new I18N();
        this.i18n.createLanguages();
        this.i18n.loadLanguages();
        this.i18n.updateLanguage();

        if(getConfig().getBoolean("checkForUpdates", true)){
            this.updateChecker = new UpdateChecker();
            this.updateChecker.checkForUpdates();
        }

        this.nms = ReflectionUtil.getVersion();
        if(this.nms == null){
            SmileyPlayerTrader.getInstance().getLogger().severe("MINECRAFT VERSION IS NOT SUPPORTED! DISABLING!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.db = new DatabaseUtil(new File(getDataFolder(), "database.db"));
        this.db.run("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY," +
                "merchant TEXT NOT NULL," +
                "cost1 BLOB," +
                "cost2 BLOB," +
                "product BLOB," +
                "enabled BOOLEAN DEFAULT 0 NOT NULL)");
        this.db.run("CREATE TABLE IF NOT EXISTS sptmeta (" +
                "sptversion INTEGER NOT NULL" +
                ")");
        this.db.upgrade();

        getCommand("smileyplayertrader").setExecutor(new CommandSmileyPlayerTrader());

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        if(this.db != null) {
            this.db.close();
        }
    }

    public DatabaseUtil getDatabase(){
        return this.db;
    }

    public I18N getI18N(){
        return this.i18n;
    }

    public UpdateChecker getUpdateChecker(){
        return this.updateChecker;
    }

    public IMCVersion getNMS(){
        return this.nms;
    }
}
