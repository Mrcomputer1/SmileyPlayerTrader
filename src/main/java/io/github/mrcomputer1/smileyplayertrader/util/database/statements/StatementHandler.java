package io.github.mrcomputer1.smileyplayertrader.util.database.statements;

import io.github.mrcomputer1.smileyplayertrader.util.database.AbstractDatabase;

import java.sql.ResultSet;

public interface StatementHandler {

    enum StatementType{
        CREATE_PRODUCT_TABLE,
        CREATE_META_TABLE,

        /**
         * merchant (uuid),
         * product (nbt blob),
         * cost1 (nbt blob),
         * cost2 (nbt blob),
         * enabled (boolean)
         */
        ADD_PRODUCT,

        /**
         * merchant (uuid)
         */
        FIND_PRODUCTS,

        /**
         * id (int)
         */
        DELETE_PRODUCT,

        /**
         * cost1 (nbt blob)
         * id (int)
         */
        SET_COST,

        /**
         * cost2 (nbt blob)
         * id (int)
         */
        SET_SECONDARY_COST,

        /**
         * product (nbt blob)
         * id (int)
         */
        SET_PRODUCT,

        /**
         * id (int)
         */
        ENABLE_PRODUCT,

        /**
         * id (int)
         */
        DISABLE_PRODUCT,

        /**
         * id (int)
         */
        GET_PRODUCT_BY_ID
    }

    void run(StatementType type, Object... objs);
    ResultSet get(StatementType type, Object... objs);

}
