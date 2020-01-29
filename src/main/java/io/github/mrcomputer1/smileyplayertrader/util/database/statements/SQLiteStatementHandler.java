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
                "enabled BOOLEAN DEFAULT 0 NOT NULL)");
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
