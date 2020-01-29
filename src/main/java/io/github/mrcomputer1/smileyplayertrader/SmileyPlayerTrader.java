package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.command.CommandSmileyPlayerTrader;
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

public class SmileyPlayerTrader extends JavaPlugin {

    public static SmileyPlayerTrader getInstance(){
        return getPlugin(SmileyPlayerTrader.class);
    }

    private AbstractDatabase db;
    private StatementHandler statementHandler;
    private I18N i18n;
    private UpdateChecker updateChecker = null;
    private IMCVersion nms = null;

    private Metrics metrics;

    @Override
    public void onEnable() {
        if(!getDescription().getVersion().contains("-SNAPSHOT")) { // Disable bStats on development versions.
            this.metrics = new Metrics(this);
        }

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

        this.db = DatabaseUtil.loadDatabase();
        this.statementHandler = this.db.getNewStatementHandler();
        if(!(this.db instanceof SQLiteDatabase) || !new File(getDataFolder(), getConfig().getString("database.file", "database.db")).exists()) {
            this.statementHandler.run(StatementHandler.StatementType.CREATE_PRODUCT_TABLE);
            this.statementHandler.run(StatementHandler.StatementType.CREATE_META_TABLE);
        }
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
}
