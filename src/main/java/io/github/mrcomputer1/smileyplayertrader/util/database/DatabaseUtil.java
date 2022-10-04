package io.github.mrcomputer1.smileyplayertrader.util.database;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.Bukkit;

import java.io.File;

public class DatabaseUtil {

    public static AbstractDatabase loadDatabase(){
        AbstractDatabase db;
        switch(SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseType()){
            case "sqlite":
                db = new SQLiteDatabase(new File(SmileyPlayerTrader.getInstance().getDataFolder(), SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseFile()));
                break;
            case "mysql":
                if(!SmileyPlayerTrader.getInstance().getConfig().contains("database.host")
                    || !SmileyPlayerTrader.getInstance().getConfig().contains("database.name")
                    || !SmileyPlayerTrader.getInstance().getConfig().contains("database.username")
                    || !SmileyPlayerTrader.getInstance().getConfig().contains("database.password")){
                    SmileyPlayerTrader.getInstance().getLogger().severe("Failed to connect to database, a required property is missing from config (host, name, username or password)");
                    Bukkit.getPluginManager().disablePlugin(SmileyPlayerTrader.getInstance());
                    return null;
                }else{
                    db = new MySQLDatabase(
                            SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseHost(),
                            SmileyPlayerTrader.getInstance().getConfiguration().getDatabasePort(),
                            SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseName(),
                            SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseUsername(),
                            SmileyPlayerTrader.getInstance().getConfiguration().getDatabasePassword()
                    );
                }
                break;
            default:
                SmileyPlayerTrader.getInstance().getLogger().severe("Invalid database type! Supported types are sqlite and mysql!");
                Bukkit.getPluginManager().disablePlugin(SmileyPlayerTrader.getInstance());
                return null;
        }
        return db;
    }

}
