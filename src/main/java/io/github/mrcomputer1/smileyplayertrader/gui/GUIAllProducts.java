package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ItemGridComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIAllProducts extends GUI {

    private static final NamespacedKey PRODUCT_ID_KEY = new NamespacedKey(SmileyPlayerTrader.getInstance(), "product_id");

    private final int page;
    private final boolean canPurchase;

    public GUIAllProducts(int page, boolean canPurchase) {
        super(I18N.translate("&2All Products (Page %0%)", page + 1), 6);

        this.page = page;
        this.canPurchase = canPurchase;

        // Item Grid
        ItemGridComponent itemGrid = new ItemGridComponent(1, 1, 7, 4);
        itemGrid.setOnClickEvent(this::onProductClick);
        this.loadItems(itemGrid);
        this.addChild(itemGrid);

        // Previous Button
        if(this.page == 0){
            this.addChild(new LabelComponent(
                    3, 5, Material.RED_STAINED_GLASS_PANE, 1,
                    I18N.translate("&cNo previous page.")
            ));
        }else{
            ButtonComponent previousBtn = new ButtonComponent(
                    3, 5, Material.ARROW, 1,
                    I18N.translate("&aPrevious Page")
            );
            previousBtn.setOnClickEvent(this::onPreviousClick);
            this.addChild(previousBtn);
        }

        // Next Button
        ButtonComponent nextBtn = new ButtonComponent(
                5, 5, Material.ARROW, 1,
                I18N.translate("&aNext Page")
        );
        nextBtn.setOnClickEvent(this::onNextClick);
        this.addChild(nextBtn);
    }

    private void loadItems(ItemGridComponent grid){
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.FIND_ALL_PRODUCTS_IN_PAGES, 28, this.page * 28
        )){
            while(set.next()){
                // Seller
                OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("merchant")));

                // Get Product
                byte[] product = set.getBytes("product");

                ItemStack is = MerchantUtil.buildItem(product);
                assert is != null;

                // Has Stock
                if(!ItemUtil.doesPlayerHaveItem(seller, is, set.getLong("id")))
                    continue;

                // Item Meta
                ItemMeta im = is.getItemMeta();
                assert im != null;

                // Product ID
                im.getPersistentDataContainer().set(PRODUCT_ID_KEY, PersistentDataType.INTEGER, set.getInt("id"));

                //
                // Product Information in Lore
                //
                List<String> lore = new ArrayList<>();

                // Product ID
                lore.add(I18N.translate("&eProduct ID: ") + set.getInt("id"));

                // Seller
                lore.add(I18N.translate("&eSeller: ") + seller.getName());

                // -Blank Line-
                lore.add("");

                // Cost (Primary)
                byte[] cost1 = set.getBytes("cost1");
                ItemStack cost1Stack = MerchantUtil.buildItem(cost1);
                assert cost1Stack != null;
                lore.add(I18N.translate("&eCost: ") + cost1Stack.getAmount() + "x " + cost1Stack.getType());

                // Cost (Secondary)
                byte[] cost2 = set.getBytes("cost2");
                if(cost2 != null) {
                    ItemStack cost2Stack = MerchantUtil.buildItem(cost2);
                    assert cost2Stack != null;
                    lore.add(I18N.translate("&eCost: ") + cost2Stack.getAmount() + "x " + cost2Stack.getType());
                }

                // -Blank Line-
                lore.add("");

                // Click to Purchase
                if(this.canPurchase)
                    lore.add(I18N.translate("&bClick to &lPurchase"));

                // Complete Item
                im.setLore(lore);
                is.setItemMeta(im);
                grid.getItems().add(is);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean onProductClick(ClickType clickType, ItemStack itemStack){
        if(itemStack == null || itemStack.getType().isAir())
            return false;

        if(clickType != ClickType.LEFT)
            return false;

        //noinspection ConstantConditions
        int id = itemStack.getItemMeta().getPersistentDataContainer().get(PRODUCT_ID_KEY, PersistentDataType.INTEGER);

        // Open Purchase UI
        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id);
        try {
            if(set.next()){
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("merchant")));

                MerchantUtil.openMerchant(this.getPlayer(), player, true, false);
            }else{
                this.getPlayer().sendMessage(I18N.translate("&cThis product is no longer for sale."));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private boolean onPreviousClick(ClickType clickType){
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIAllProducts(this.page - 1, this.canPurchase));
        return false;
    }

    private boolean onNextClick(ClickType clickType){
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIAllProducts(this.page + 1, this.canPurchase));
        return false;
    }

}
