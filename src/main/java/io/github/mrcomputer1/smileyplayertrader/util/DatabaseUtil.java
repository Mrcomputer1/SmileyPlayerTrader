package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;

public class DatabaseUtil {

    private Connection conn = null;
    private long insertId = -1;

    public DatabaseUtil(File name){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + name.getAbsolutePath());
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to open/create SQLite3 database. Disabling...");
            Bukkit.getPluginManager().disablePlugin(SmileyPlayerTrader.getInstance());
            e.printStackTrace();
        }
    }

    public long getInsertId(){
        return this.insertId;
    }

    private void setValues(PreparedStatement stmt, Object... objs){
        for(int i = 0; i < objs.length; i++){
            try {
                stmt.setObject(i + 1, objs[i]);
            } catch (SQLException e) {
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to set a value in an SQLite3 statement.");
                e.printStackTrace();
            }
        }
    }

    public void run(String sql, Object... objs){
        try {
            if (!isConnected()) {
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to run statement as there is no database connection.");
                return;
            }
            PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setValues(stmt, objs);
            stmt.execute();
            ResultSet s = stmt.getGeneratedKeys();
            if(s.next()){
                this.insertId = s.getLong(1);
            }
        }catch(SQLException e){
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to execute SQLite3 statement.");
            e.printStackTrace();
        }
    }

    public ResultSet get(String sql, Object... objs){
        try{
            if(!isConnected()){
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to run statement as there is no database connection.");
                return null;
            }
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            setValues(stmt, objs);
            return stmt.executeQuery();
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to execute SQLite3 query statement.");
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isConnected(){
        try {
            return this.conn != null && !this.conn.isClosed();
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().warning("Failed to check connection status, assuming not connected!");
            e.printStackTrace();
            return false;
        }
    }

    public void close(){
        if(!isConnected()){
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to close connection as not connected!");
            return;
        }
        try {
            this.conn.close();
        } catch (SQLException e) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to close connection.");
            e.printStackTrace();
        }
    }

}
