package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.bedrock.BedrockGUIEditProduct;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.BackgroundComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ItemGridComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.productmanage.*;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GUIProductList extends GUI {

    private static final NamespacedKey PRODUCT_ID_KEY = new NamespacedKey(SmileyPlayerTrader.getInstance(), "product_id");

    private final OfflinePlayer target;
    private final int page;
    private final boolean isMine;

    private final ButtonComponent collectEarningsBtn;

    public GUIProductList(Player uiPlayer, OfflinePlayer target, int page, boolean isMine) {
        super(
                isMine
                        ? I18N.translate("&2My Products (Page %0%)", page + 1)
                        : I18N.translate("&2%0%'s Products (Page %1%)", target.getName(), page + 1),
                6
        );

        if(GeyserUtil.isBedrockPlayer(uiPlayer))
            this.setBackgroundFillItem(GUI.BACKGROUND_BEDROCK);

        this.target = target;
        this.page = page;
        this.isMine = isMine;

        // Menu Bar
        this.addChild(new BackgroundComponent(
                0, 5, 9, 1,
                Material.IRON_BARS, 1, ChatColor.RESET.toString()
        ));

        // Item Grid
        ItemGridComponent itemGrid = new ItemGridComponent(1, 1, 7, 4);
        itemGrid.setOnClickEvent(this::onProductClick);
        this.loadItems(itemGrid, uiPlayer);
        this.addChild(itemGrid);

        // Collect Earnings Button
        if(
                SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageEnabled()
                && this.isMine
        ){
            this.collectEarningsBtn = new ButtonComponent(
                    0, 5, Material.CHEST, 1,
                    I18N.translate("&eCollect All Earnings")
            );
            this.collectEarningsBtn.setOnClickEvent(this::onCollectEarningsClick);
            this.addChild(this.collectEarningsBtn);

            // Check if uncollected earnings
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_UNCOLLECTED_EARNINGS, this.target.getUniqueId().toString())){
                if(set.next()){
                    if(set.getInt("uncollected_earnings") > 0){
                        this.collectEarningsBtn.getLore().add(I18N.translate("&a&l&k# &r&a&lUncollected Earnings &a&l&k#"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else this.collectEarningsBtn = null;

        // Preview Button
        if(this.isMine) {
            ButtonComponent previewBtn = new ButtonComponent(
                    8, 5, Material.VILLAGER_SPAWN_EGG, 1,
                    I18N.translate("&aPreview Store")
            );
            previewBtn.setOnClickEvent(this::onPreviewClick);
            this.addChild(previewBtn);
        }

        // Previous Button
        if(this.page == 0) {
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

        // Create Product Button
        ButtonComponent createBtn = new ButtonComponent(
                4, 5, Material.EMERALD, 1,
                I18N.translate("&aCreate New Product")
        );
        createBtn.setOnClickEvent(this::onCreateClick);
        this.addChild(createBtn);
    }

    private void loadItems(ItemGridComponent grid, Player uiPlayer){
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.FIND_PRODUCTS_IN_PAGES, this.target.getUniqueId().toString(), 28, this.page * 28
        )){
            while(set.next()){
                // Get Product
                byte[] product = set.getBytes("product");

                ItemStack is;
                if(product == null){
                    is = new ItemStack(Material.BARRIER);
                }else{
                    is = MerchantUtil.buildItem(product);
                }
                assert is != null;

                // Item Meta
                ItemMeta im = is.getItemMeta();
                assert im != null;

                // Product ID
                im.getPersistentDataContainer().set(PRODUCT_ID_KEY, PersistentDataType.INTEGER, set.getInt("id"));

                // Unset Product
                if(product == null)
                    im.setDisplayName(I18N.translate("&cProduct Not Set!"));

                // Product Information in Lore
                List<String> lore = new ArrayList<>();

                lore.add(I18N.translate("&eProduct ID: ") + set.getInt("id"));
                lore.add(I18N.translate("&bClick to &lEdit"));

                if(!GeyserUtil.isBedrockPlayer(uiPlayer)) {
                    if (set.getBoolean("enabled") && set.getBoolean("available")) {
                        lore.add(I18N.translate("&bRight Click to &lDisable/Hide"));
                    } else {
                        lore.add(I18N.translate("&bRight Click to &lEnable/Show"));
                    }

                    if (SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageEnabled()) {
                        lore.add(I18N.translate("&bShift Click to &lManage Stored Items"));
                    }

                    lore.add(I18N.translate("&bDrop to &lDelete"));
                }

                // Complete Item
                im.setLore(lore);
                is.setItemMeta(im);
                grid.getItems().add(is);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean onProductClick(ClickType clickType, ItemStack itemStack) {
        if(itemStack == null || itemStack.getType().isAir())
            return false;

        //noinspection ConstantConditions
        int id = itemStack.getItemMeta().getPersistentDataContainer().get(PRODUCT_ID_KEY, PersistentDataType.INTEGER);

        if(clickType == ClickType.LEFT){

            // Edit or Bedrock Multi Choice
            ProductState state = new ProductState(this.page, this.target, this.isMine, id);
            if(GeyserUtil.isBedrockPlayer(this.getPlayer())) {
                // Bedrock Multi Choice
                this.getPlayer().closeInventory();
                GeyserUtil.showFormDelayed(this.getPlayer(), new BedrockGUIEditProduct(this.getPlayer(), state));
            }else{
                // Edit
                GUIManager.getInstance().openGui(this.getPlayer(), new GUIProduct(this.getPlayer(), state));
            }

        }else if(clickType == ClickType.DROP){

            if(GeyserUtil.isBedrockPlayer(this.getPlayer()))
                return false;

            // Delete
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)){
                if(set.next()) {
                    if(set.getInt("stored_product") > 0 || set.getInt("stored_cost") > 0 || set.getInt("stored_cost2") > 0){
                        GUIManager.sendErrorMessage(this.getPlayer(), I18N.translate("&cYou must withdraw all stored product and earnings before deleting the product."));
                        return false;
                    }
                    GUIManager.getInstance().openGui(this.getPlayer(), new GUIDeleteProduct(id, this.page, this.target, this.isMine));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }else if(clickType == ClickType.RIGHT){

            if(GeyserUtil.isBedrockPlayer(this.getPlayer()))
                return false;

            // Enable/Disable/Hide
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_ENABLED, id)){
                if(set.next()) {
                    boolean enable = !set.getBoolean("enabled") || !set.getBoolean("available");
                    GUIManager.getInstance().openGui(this.getPlayer(), new GUIEnableDisableProduct(id, enable, this.page, this.target, this.isMine));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }else if(clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT){

            if(GeyserUtil.isBedrockPlayer(this.getPlayer()))
                return false;

            // Manage Stored Items
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)){
                if(set.next()) {
                    int stored = set.getInt("stored_product");

                    byte[] productBytes = set.getBytes("product");
                    if(productBytes == null){
                        return false;
                    }
                    ItemStack productStack = MerchantUtil.buildItem(productBytes);

                    GUIManager.getInstance().openGui(this.getPlayer(), new GUIItemStorage(this.getPlayer(), id, stored, productStack, this.page, this.target, this.isMine));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return false;
    }

    private boolean onCreateClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProduct(this.getPlayer(), new ProductState(this.page, this.target, this.isMine)));
        return false;
    }

    private boolean onNextClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page + 1, this.isMine));
        return false;
    }

    private boolean onPreviousClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page - 1, this.isMine));
        return false;
    }

    private boolean onCollectEarningsClick(ClickType clickType){
        if(ItemUtil.collectEarnings(this.getPlayer())){
            if(!GeyserUtil.isBedrockPlayer(this.getPlayer()))
                this.getPlayer().sendMessage(I18N.translate("&aCollected earnings."));
        }else{
            GUIManager.sendErrorMessage(this.getPlayer(), I18N.translate("&cYou have no earnings to collect."));
        }

        this.collectEarningsBtn.getLore().clear();
        this.refreshComponent(this.collectEarningsBtn);
        return false;
    }

    private boolean onPreviewClick(ClickType clickType){
        MerchantUtil.openPreviewMerchant(this.getPlayer());
        return false;
    }

}
