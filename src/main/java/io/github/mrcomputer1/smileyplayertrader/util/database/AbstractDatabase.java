package io.github.mrcomputer1.smileyplayertrader.util.database;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.MySQLStatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.SQLiteStatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDatabase {

    protected static int dbVersion = 6;

    public abstract long getInsertId();
    public abstract void run(String sql, Object... objs);
    public abstract ResultSet get(String sql, Object... objs);
    public abstract boolean isConnected();
    public abstract void close();

    public String getDatabasePrefix(){
        return SmileyPlayerTrader.getInstance().getConfiguration().getDatabasePrefix();
    }

    public StatementHandler getNewStatementHandler(){
        if(SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseType().equals("sqlite")){
            return new SQLiteStatementHandler((SQLiteDatabase)this);
        }else if(SmileyPlayerTrader.getInstance().getConfiguration().getDatabaseType().equals("mysql")){
            return new MySQLStatementHandler((MySQLDatabase)this);
        }else return null;
    }

    // Upgrade Policy
    // The last FIVE versions will be upgradable. For example if you are on version 2 and using a version 1 database, that will upgrade.
    // However, if you are on version 6 and using a version 1 database, that will not upgrade and the plugin will not load.
    public void upgrade(){
        try(ResultSet set = get("SELECT * FROM " + this.getDatabasePrefix() + "meta")) {
            if (set.next()) {
                int ver = set.getInt("sptversion");
                if(ver < dbVersion){ // if the db version is older than the supported db version
                    SmileyPlayerTrader.getInstance().getLogger().warning("Upgrading to database version " + dbVersion);

                    while(ver <= dbVersion){
                        upgrade(ver);
                        ver++;
                    }

                    SmileyPlayerTrader.getInstance().getLogger().warning("Upgraded to database version " + dbVersion);
                    run("UPDATE " + this.getDatabasePrefix() + "meta SET sptversion=?", dbVersion);
                }else if(ver > dbVersion){ // if the db version is newer than the supported db version
                    SmileyPlayerTrader.getInstance().getLogger().warning("You are loading a database meant for a newer plugin version!");
                }
            }else{
                run("INSERT INTO " + this.getDatabasePrefix() + "meta (sptversion) VALUES (?)", dbVersion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract void upgrade(int version); // version is old version

}
