package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.RegionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.SPTPlaceholderExpansions;
import io.github.mrcomputer1.smileyplayertrader.util.database.AbstractDatabase;
import io.github.mrcomputer1.smileyplayertrader.util.database.DatabaseUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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
    private BugWarner bugWarner = null;
    private VersionSupport versionSupport = null;
    private SPTConfiguration configuration = null;
    private GUIManager guiManager = null;

    private Metrics metrics = null;

    @Override
    public void onEnable() {
        // Configuration
        saveDefaultConfig();
        this.configuration = new SPTConfiguration(getConfig());

        if(this.configuration.hasLegacyDisabledWorldsAndAllowedWorldsConfig())
            getLogger().warning("The configuration file specifies both the legacy 'disabledWorlds' configuration setting " +
                    "and the newer 'allowedWorlds' setting. The 'allowedWorlds.worlds' list will be prioritised over the " +
                    "legacy 'disabledWorlds' setting.");

        // bStats
        if(!getDescription().getVersion().contains("-SNAPSHOT")) { // Disable bStats on development versions.
            this.metrics = new Metrics(this, BSTATS_PLUGIN_ID);

            this.metrics.addCustomChart(new SimplePie("database_system", new Callable<String>() {
                @Override
                public String call() {
                    switch(getConfiguration().getDatabaseType()){
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

        // I18N
        this.i18n = new I18N();
        this.i18n.createLanguages();
        this.i18n.loadLanguages();
        this.i18n.updateLanguage();

        // Update Checker
        if(getConfiguration().getCheckForUpdatesEnabled()){
            this.updateChecker = new UpdateChecker();
            this.updateChecker.checkForUpdates();
        }

        // Bug Warner
        if(getConfiguration().getCheckForBugsEnabled()){
            this.bugWarner = new BugWarner();
            boolean b = this.bugWarner.checkForBugs();
            if(b)
                return;
        }

        // Version Support
        this.versionSupport = new VersionSupport();
        try {
            this.versionSupport.bindCompatibleVersion();
        } catch (IllegalStateException ex) {
            SmileyPlayerTrader.getInstance().getLogger().severe("MINECRAFT VERSION IS NOT SUPPORTED! DISABLING!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Database
        boolean shouldCreateTables = true;
        if(this.getConfiguration().getDatabaseType().equals("sqlite")){
            if(new File(this.getDataFolder(), this.getConfiguration().getDatabaseFile()).exists()){
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

        // Player Configuration
        this.playerConfig = new PlayerConfig();
        if(Bukkit.getOnlinePlayers().size() != 0)
            this.playerConfig.reloadPlayers();

        // Commands
        CommandSmileyPlayerTrader cspt = new CommandSmileyPlayerTrader();
        getCommand("smileyplayertrader").setExecutor(cspt);
        getCommand("smileyplayertrader").setTabCompleter(cspt);

        // GUIs
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeEventListener(), this);
        if(getConfiguration().getUseGuiManager()){
            this.guiManager = new GUIManager();
            Bukkit.getPluginManager().registerEvents(this.guiManager, this);
        }

        // PlaceholderAPI
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new SPTPlaceholderExpansions().register();
        }
    }

    @Override
    public void onLoad() {
        // Regions
        RegionUtil.setup();
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

    public BugWarner getBugWarner(){
        return this.bugWarner;
    }

    public VersionSupport getVersionSupport(){
        return this.versionSupport;
    }

    public PlayerConfig getPlayerConfig(){
        return this.playerConfig;
    }

    public SPTConfiguration getConfiguration() {
        return this.configuration;
    }

    public GUIManager getGuiManager() {
        return this.guiManager;
    }

}
