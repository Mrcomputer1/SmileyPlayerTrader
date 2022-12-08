package io.github.mrcomputer1.smileyplayertrader.util.database.statements;

import io.github.mrcomputer1.smileyplayertrader.util.database.AbstractDatabase;
import io.github.mrcomputer1.smileyplayertrader.util.database.SQLiteDatabase;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SQLiteStatementHandler implements StatementHandler {

    private Map<StatementType, String> statements = new HashMap<>();
    private GenericStatementHandler generic;
    private AbstractDatabase database;

    public SQLiteStatementHandler(SQLiteDatabase db){
        this.database = db;
        this.generic = new GenericStatementHandler(database);

        statements.put(StatementType.CREATE_PRODUCT_TABLE, "CREATE TABLE IF NOT EXISTS $prefix$products (" +
                "id INTEGER PRIMARY KEY," +
                "merchant TEXT NOT NULL," +
                "cost1 BLOB," +
                "cost2 BLOB," +
                "product BLOB," +
                "enabled BOOLEAN DEFAULT 0 NOT NULL," +
                "available BOOLEAN DEFAULT 0 NOT NULL," +
                "special_price INTEGER DEFAULT 0 NOT NULL," +
                "priority INTEGER DEFAULT 0 NOT NULL," +
                "stored_product INTEGER DEFAULT 0 NOT NULL," +
                "stored_cost INTEGER DEFAULT 0 NOT NULL," +
                "stored_cost2 INTEGER DEFAULT 0 NOT NULL," +
                "hide_on_out_of_stock INTEGER DEFAULT 0 NOT NULL," +
                "purchase_limit INTEGER DEFAULT -1 NOT NULL," +
                "purchase_count INTEGER DEFAULT 0 NOT NULL)");
        statements.put(StatementType.CREATE_SETTINGS_TABLE, "CREATE TABLE IF NOT EXISTS $prefix$settings (" +
                "player TEXT NOT NULL," +
                "trade_toggle BOOLEAN DEFAULT 1 NOT NULL," +
                "combat_notice_toggle BOOLEAN DEFAULT 1 NOT NULL)");
    }

    @Override
    public void run(StatementType type, Object... objs) {
        if(statements.containsKey(type)){
            String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
            database.run(statement, objs);
        }else{
            this.generic.run(type, objs);
        }
    }

    @Override
    public ResultSet get(StatementType type, Object... objs) {
        if(statements.containsKey(type)){
            String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
            return database.get(statement, objs);
        }else{
            return this.generic.get(type, objs);
        }
    }

}
