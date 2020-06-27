package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIEventListener;
import io.github.mrcomputer1.smileyplayertrader.util.database.AbstractDatabase;
import io.github.mrcomputer1.smileyplayertrader.util.database.DatabaseUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.IMCVersion;
import io.github.mrcomputer1.smileyplayertrader.util.database.SQLiteDatabase;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ReflectionUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

public class SmileyPlayerTrader extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 6187;

    public static SmileyPlayerTrader getInstance(){
        return getPlugin(SmileyPlayerTrader.class);
    }

    private AbstractDatabase db;
    private StatementHandler statementHandler;
    private PlayerConfig playerConfig;
    private I18N i18n;
    private UpdateChecker updateChecker = null;
    private IMCVersion nms = null;

    private Metrics metrics = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if(!getDescription().getVersion().contains("-SNAPSHOT")) { // Disable bStats on development versions.
            this.metrics = new Metrics(this, BSTATS_PLUGIN_ID);

            this.metrics.addCustomChart(new Metrics.SimplePie("database_system", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    switch(getConfig().getString("database.type", "sqlite")){
                        case "sqlite":
                            return "SQLite";
                        case "mysql":
                            return "MySQL";
                        default:
                            return "Unknown";
                    }
                }
            }));
        }

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

        boolean shouldCreateTables = true;
        if(this.getConfig().getString("database.type", "sqlite").equals("sqlite")){
            if(new File(this.getDataFolder(), this.getConfig().getString("database.file", "database.db")).exists()){
                shouldCreateTables = false;
            }
        }

        this.db = DatabaseUtil.loadDatabase();
        this.statementHandler = this.db.getNewStatementHandler();
        if(shouldCreateTables) {
            this.statementHandler.run(StatementHandler.StatementType.CREATE_PRODUCT_TABLE);
            this.statementHandler.run(StatementHandler.StatementType.CREATE_META_TABLE);
            this.statementHandler.run(StatementHandler.StatementType.CREATE_SETTINGS_TABLE);
        }
        this.db.upgrade();

        this.playerConfig = new PlayerConfig();
        if(Bukkit.getOnlinePlayers().size() != 0)
            this.playerConfig.reloadPlayers();

        CommandSmileyPlayerTrader cspt = new CommandSmileyPlayerTrader();
        getCommand("smileyplayertrader").setExecutor(cspt);
        getCommand("smileyplayertrader").setTabCompleter(cspt);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        if(getConfig().getBoolean("useGuiManager", true)){
            Bukkit.getPluginManager().registerEvents(new GUIEventListener(), this);
        }
    }

    @Override
    public void onDisable() {
        if(this.db != null) {
            this.db.close();
        }
    }

    public AbstractDatabase getDatabase(){
        return this.db;
    }

    public StatementHandler getStatementHandler(){
        return this.statementHandler;
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

    public PlayerConfig getPlayerConfig(){
        return this.playerConfig;
    }
}
