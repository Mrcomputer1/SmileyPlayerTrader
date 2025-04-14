package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductState {

    private static final ItemStack TEMPLATE_ITEM = new ItemStack(Material.AIR);

    // Product Details
    public final boolean isNew;
    public final int id;
    public final boolean startedValid; // product and cost were valid (not null) when editing started

    // Items
    public ItemStack stack = TEMPLATE_ITEM.clone();
    public ItemStack costStack = TEMPLATE_ITEM.clone();
    public ItemStack costStack2 = TEMPLATE_ITEM.clone();

    // Properties
    public int discount = 0;
    public int priority = 0;
    public boolean hideOnOutOfStock = false;
    public int purchaseLimit = -1;

    // Storage
    public int storedProduct = 0;
    public int storedCost = 0;
    public int storedCost2 = 0;

    // Memory
    public final int page;
    public final OfflinePlayer target;
    public final boolean isMine;

    public ProductState(int page, OfflinePlayer target, boolean isMine){
        this.page = page;
        this.target = target;
        this.isMine = isMine;

        this.isNew = true;
        this.id = -1;
        this.startedValid = false;

        switch(SmileyPlayerTrader.getInstance().getConfiguration().getOutOfStockBehaviour()){
            case HIDE_BY_DEFAULT:
            case HIDE:
                this.hideOnOutOfStock = true;
                break;
            case SHOW_BY_DEFAULT:
            case SHOW:
                this.hideOnOutOfStock = false;
                break;
        }
    }

    public ProductState(int page, OfflinePlayer target, boolean isMine, int id){
        this.page = page;
        this.target = target;
        this.isMine = isMine;

        this.isNew = false;
        this.id = id;

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, this.id)){
            if(set.next()){

                byte[] productStackData = set.getBytes("product");
                this.setStack(productStackData == null ? null : MerchantUtil.buildItem(productStackData));

                byte[] costStackData = set.getBytes("cost1");
                this.setCostStack(costStackData == null ? null : MerchantUtil.buildItem(costStackData));

                byte[] costStack2Data = set.getBytes("cost2");
                this.setCostStack2(costStack2Data == null ? null : MerchantUtil.buildItem(costStack2Data));

                this.discount = set.getInt("special_price");
                this.priority = set.getInt("priority");
                this.hideOnOutOfStock = set.getBoolean("hide_on_out_of_stock");
                this.purchaseLimit = set.getInt("purchase_limit");

                this.storedProduct = set.getInt("stored_product");
                this.storedCost = set.getInt("stored_cost");
                this.storedCost2 = set.getInt("stored_cost2");

                this.startedValid = productStackData != null && costStackData != null;

            }else throw new RuntimeException("Invalid ID.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        if(this.stack == null)
            this.stack = TEMPLATE_ITEM.clone();
    }

    public void setCostStack(ItemStack costStack) {
        this.costStack = costStack;
        if(this.costStack == null)
            this.costStack = TEMPLATE_ITEM.clone();
    }

    public void setCostStack2(ItemStack costStack2) {
        this.costStack2 = costStack2;
        if(this.costStack2 == null)
            this.costStack2 = TEMPLATE_ITEM.clone();
    }

}
