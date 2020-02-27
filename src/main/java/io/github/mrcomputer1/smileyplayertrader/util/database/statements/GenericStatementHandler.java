package io.github.mrcomputer1.smileyplayertrader.util.database.statements;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.AbstractDatabase;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GenericStatementHandler implements StatementHandler {

    private Map<StatementType, String> statements = new HashMap<>();
    private AbstractDatabase database;

    public GenericStatementHandler(AbstractDatabase db){
        this.database = db;

        statements.put(StatementType.CREATE_META_TABLE, "CREATE TABLE IF NOT EXISTS $prefix$meta (" +
                "sptversion INTEGER NOT NULL" +
                ")");
        statements.put(StatementType.ADD_PRODUCT, "INSERT INTO $prefix$products (merchant, product, cost1, cost2, enabled) VALUES (?, ?, ?, ?, ?)");
        statements.put(StatementType.FIND_PRODUCTS, "SELECT * FROM $prefix$products WHERE merchant=?");
        statements.put(StatementType.DELETE_PRODUCT, "DELETE FROM $prefix$products WHERE id=?");
        statements.put(StatementType.SET_COST, "UPDATE $prefix$products SET cost1=? WHERE id=?");
        statements.put(StatementType.SET_SECONDARY_COST, "UPDATE $prefix$products SET cost2=? WHERE id=?");
        statements.put(StatementType.SET_PRODUCT, "UPDATE $prefix$products SET product=? WHERE id=?");
        statements.put(StatementType.ENABLE_PRODUCT, "UPDATE $prefix$products SET enabled=1 WHERE id=?");
        statements.put(StatementType.DISABLE_PRODUCT, "UPDATE $prefix$products SET enabled=0 WHERE id=?");
        statements.put(StatementType.GET_PRODUCT_BY_ID, "SELECT * FROM $prefix$products WHERE id=?");
        statements.put(StatementType.FIND_PRODUCTS_IN_PAGES, "SELECT id, product, enabled FROM $prefix$products WHERE merchant=? LIMIT ? OFFSET ?");
        statements.put(StatementType.SET_PRODUCT_COST_COST2, "UPDATE $prefix$products SET product=?, cost1=?, cost2=? WHERE id=?");
        statements.put(StatementType.GET_ENABLED, "SELECT enabled FROM $prefix$products WHERE id=?");
    }

    @Override
    public void run(StatementType type, Object... objs) {
        String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
        database.run(statement, objs);
    }

    @Override
    public ResultSet get(StatementType type, Object... objs) {
        String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
        return database.get(statement, objs);
    }

}
