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
        statements.put(StatementType.ADD_PRODUCT, "INSERT INTO $prefix$products (merchant, product, cost1, cost2, enabled, available, special_price, priority, hide_on_out_of_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statements.put(StatementType.FIND_PRODUCTS, "SELECT * FROM $prefix$products WHERE merchant=? ORDER BY priority DESC");
        statements.put(StatementType.DELETE_PRODUCT, "DELETE FROM $prefix$products WHERE id=?");
        statements.put(StatementType.SET_COST, "UPDATE $prefix$products SET cost1=?, special_price=0 WHERE id=?");
        statements.put(StatementType.SET_SECONDARY_COST, "UPDATE $prefix$products SET cost2=? WHERE id=?");
        statements.put(StatementType.SET_PRODUCT, "UPDATE $prefix$products SET product=? WHERE id=?");
        statements.put(StatementType.ENABLE_PRODUCT, "UPDATE $prefix$products SET enabled=1, available=1 WHERE id=?");
        statements.put(StatementType.DISABLE_PRODUCT, "UPDATE $prefix$products SET available=0 WHERE id=?");
        statements.put(StatementType.GET_PRODUCT_BY_ID, "SELECT * FROM $prefix$products WHERE id=?");
        statements.put(StatementType.FIND_PRODUCTS_IN_PAGES, "SELECT id, product, enabled, available FROM $prefix$products WHERE merchant=? ORDER BY priority DESC LIMIT ? OFFSET ?");
        statements.put(StatementType.SET_PRODUCT_COST_COST2_SPECIALPRICE_PRIORITY_HIDEOUTOFSTOCK_PURCHASELIMIT, "UPDATE $prefix$products SET product=?, cost1=?, cost2=?, special_price=?, priority=?, hide_on_out_of_stock=?, purchase_limit=? WHERE id=?");
        statements.put(StatementType.GET_ENABLED, "SELECT enabled, available FROM $prefix$products WHERE id=?");
        statements.put(StatementType.LOAD_PLAYER_CONFIG, "SELECT * FROM $prefix$settings WHERE player=?");
        statements.put(StatementType.CREATE_DEFAULT_PLAYER_CONFIG, "INSERT INTO $prefix$settings (player) VALUES (?)");
        statements.put(StatementType.UPDATE_PLAYER_CONFIG, "UPDATE $prefix$settings SET trade_toggle=?, combat_notice_toggle=? WHERE player=?");
        statements.put(StatementType.HIDE_PRODUCT, "UPDATE $prefix$products SET enabled=0 WHERE id=?");
        statements.put(StatementType.SET_DISCOUNT, "UPDATE $prefix$products SET special_price=? WHERE id=?");
        statements.put(StatementType.SET_PRIORITY, "UPDATE $prefix$products SET priority=? WHERE id=?");
        statements.put(StatementType.CHANGE_STORED_PRODUCT, "UPDATE $prefix$products SET stored_product=stored_product + ? WHERE id=?");
        statements.put(StatementType.CHANGE_STORED_COST, "UPDATE $prefix$products SET stored_cost=stored_cost + ? WHERE id=?");
        statements.put(StatementType.CHANGE_STORED_COST2, "UPDATE $prefix$products SET stored_cost2=stored_cost2 + ? WHERE id=?");
        statements.put(StatementType.SET_STORED_COST, "UPDATE $prefix$products SET stored_cost=? WHERE id=?");
        statements.put(StatementType.SET_STORED_COST2, "UPDATE $prefix$products SET stored_cost2=? WHERE id=?");
        statements.put(StatementType.FIND_PRODUCTS_WITH_EARNINGS, "SELECT * FROM $prefix$products WHERE merchant=? AND stored_cost>0 OR stored_cost2>0");
        statements.put(StatementType.GET_UNCOLLECTED_EARNINGS, "SELECT COUNT(*) AS uncollected_earnings FROM $prefix$products WHERE merchant=? AND stored_cost>0 OR stored_cost2>0");
        statements.put(StatementType.SET_HIDE_ON_OUT_OF_STOCK, "UPDATE $prefix$products SET hide_on_out_of_stock=? WHERE id=?");
        statements.put(StatementType.FIND_ALL_PRODUCTS_IN_PAGES, "SELECT * FROM $prefix$products WHERE product IS NOT NULL AND cost1 IS NOT NULL AND enabled=1 AND available=1 AND id>? AND (purchase_limit=-1 OR purchase_count<purchase_limit) LIMIT ? OFFSET ?");
        statements.put(StatementType.INCREMENT_PURCHASE_COUNT, "UPDATE $prefix$products SET purchase_count=purchase_count + 1 WHERE id=?");
        statements.put(StatementType.RESET_PURCHASE_COUNT, "UPDATE $prefix$products SET purchase_count=0 WHERE id=?");
        statements.put(StatementType.SET_PURCHASE_LIMIT, "UPDATE $prefix$products SET purchase_limit=? WHERE id=?");
    }

    @Override
    public void run(StatementType type, Object... objs) {
        String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
        database.run(statement, objs);
    }

    @Override
    public long runAndReturnInsertId(StatementType type, Object... objs) {
        String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
        return database.runAndReturnInsertId(statement, objs);
    }

    @Override
    public ResultSet get(StatementType type, Object... objs) {
        String statement = statements.get(type).replace("$prefix$", database.getDatabasePrefix());
        return database.get(statement, objs);
    }

}
