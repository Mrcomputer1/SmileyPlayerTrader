package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemStorageStockLocation implements IStockLocation{

    @Override
    public ItemStack giveEarnings(OfflinePlayer player, ItemStack stack, long id, boolean primaryCost) {
        if(!primaryCost)
            return null;

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.CHANGE_STORED_COST, 1, id);
        return null;
    }

    @Override
    public ItemStack removeStock(OfflinePlayer player, ItemStack stack, long id) {
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.CHANGE_STORED_PRODUCT, -1, id);
        return null;
    }

    @Override
    public int doesPlayerHaveItem(OfflinePlayer player, ItemStack stack, long id) {
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)) {
            if(set.next()){
                ItemStack product = VersionSupport.byteArrayToItemStack(set.getBytes("product"));
                return set.getInt("stored_product") * product.getAmount();
            }else{
                return 0;
            }
        } catch (SQLException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAvailable(OfflinePlayer player) {
        return SmileyPlayerTrader.getInstance().getConfig().getBoolean("itemStorage.enable", true);
    }

}
