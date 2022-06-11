package io.github.mrcomputer1.smileyplayertrader.util.database.statements;

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
        statements.put(StatementType.ADD_PRODUCT, "INSERT INTO $prefix$products (merchant, product, cost1, cost2, enabled, available, special_price) VALUES (?, ?, ?, ?, ?, ?, ?)");
        statements.put(StatementType.FIND_PRODUCTS, "SELECT * FROM $prefix$products WHERE merchant=? ORDER BY priority DESC");
        statements.put(StatementType.DELETE_PRODUCT, "DELETE FROM $prefix$products WHERE id=?");
        statements.put(StatementType.SET_COST, "UPDATE $prefix$products SET cost1=?, special_price=0 WHERE id=?");
        statements.put(StatementType.SET_SECONDARY_COST, "UPDATE $prefix$products SET cost2=? WHERE id=?");
        statements.put(StatementType.SET_PRODUCT, "UPDATE $prefix$products SET product=? WHERE id=?");
        statements.put(StatementType.ENABLE_PRODUCT, "UPDATE $prefix$products SET enabled=1, available=1 WHERE id=?");
        statements.put(StatementType.DISABLE_PRODUCT, "UPDATE $prefix$products SET available=0 WHERE id=?");
        statements.put(StatementType.GET_PRODUCT_BY_ID, "SELECT * FROM $prefix$products WHERE id=?");
        statements.put(StatementType.FIND_PRODUCTS_IN_PAGES, "SELECT id, product, enabled, available FROM $prefix$products WHERE merchant=? ORDER BY priority DESC LIMIT ? OFFSET ?");
        statements.put(StatementType.SET_PRODUCT_COST_COST2_SPECIALPRICE_PRIORITY, "UPDATE $prefix$products SET product=?, cost1=?, cost2=?, special_price=?, priority=? WHERE id=?");
        statements.put(StatementType.GET_ENABLED, "SELECT enabled, available FROM $prefix$products WHERE id=?");
        statements.put(StatementType.LOAD_PLAYER_CONFIG, "SELECT * FROM $prefix$settings WHERE player=?");
        statements.put(StatementType.CREATE_DEFAULT_PLAYER_CONFIG, "INSERT INTO $prefix$settings (player) VALUES (?)");
        statements.put(StatementType.UPDATE_PLAYER_CONFIG, "UPDATE $prefix$settings SET trade_toggle=?, combat_notice_toggle=? WHERE player=?");
        statements.put(StatementType.HIDE_PRODUCT, "UPDATE $prefix$products SET enabled=0 WHERE id=?");
        statements.put(StatementType.SET_DISCOUNT, "UPDATE $prefix$products SET special_price=? WHERE id=?");
        statements.put(StatementType.SET_PRIORITY, "UPDATE $prefix$products SET priority=? WHERE id=?");
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
