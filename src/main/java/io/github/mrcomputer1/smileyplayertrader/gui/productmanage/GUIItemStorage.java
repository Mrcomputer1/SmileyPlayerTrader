package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class GUIItemStorage extends GUI {

    private final int id;
    private int storedProduct;
    private final ItemStack product;

    private final int page;
    private final OfflinePlayer target;
    private final boolean isMine;

    private final LabelComponent infoLabel;

    public GUIItemStorage(Player uiPlayer, int id, int storedProduct, ItemStack product, int page, OfflinePlayer target, boolean isMine) {
        super(I18N.translate("&2Manage Stored Items"), 6);

        if(GeyserUtil.isBedrockPlayer(uiPlayer))
            this.setBackgroundFillItem(GUI.BACKGROUND_BEDROCK);

        this.id = id;
        this.page = page;
        this.target = target;
        this.isMine = isMine;

        this.storedProduct = storedProduct;
        this.product = product;

        this.setAllowInteractingWithPlayerInventory(true);

        // Info Label
        this.infoLabel = new LabelComponent(
                4, 1,
                GeyserUtil.isBedrockPlayer(uiPlayer) ? Material.CHEST_MINECART : Material.YELLOW_STAINED_GLASS_PANE,
                1,
                I18N.translate("&eStored Product: %0%", this.storedProduct),
                I18N.translate("&eClick on the product in your inventory to store it.")
        );
        this.addChild(this.infoLabel);

        // Withdraw 1
        ButtonComponent withdraw1 = new ButtonComponent(
                2, 3, Material.CHEST, 1, I18N.translate("&eWithdraw 1")
        );
        withdraw1.setOnClickEvent(this::onWithdraw1Click);
        this.addChild(withdraw1);

        // Withdraw 8
        ButtonComponent withdraw8 = new ButtonComponent(
                4, 3, Material.CHEST, 8, I18N.translate("&eWithdraw 8")
        );
        withdraw8.setOnClickEvent(this::onWithdraw8Click);
        this.addChild(withdraw8);

        // Withdraw All
        ButtonComponent withdrawAll = new ButtonComponent(
                6, 3, Material.CHEST, 64, I18N.translate("&eWithdraw All")
        );
        withdrawAll.setOnClickEvent(this::onWithdrawAllClick);
        this.addChild(withdrawAll);

        // OK
        ButtonComponent okBtn = new ButtonComponent(
                0, 5, Material.EMERALD_BLOCK, 1, I18N.translate("&aOK")
        );
        okBtn.setOnClickEvent(this::onOkClick);
        this.addChild(okBtn);
    }

    private void withdraw(int limit){
        ItemStack stack = this.product.clone();

        int countAvailable = this.storedProduct;

        if(limit > countAvailable){
            limit = countAvailable;
        }

        if(limit <= 0){
            GUIManager.sendErrorMessage(this.getPlayer(), I18N.translate("&cYou do not have enough of that product."));
            return;
        }

        stack.setAmount(limit);

        Map<Integer, ItemStack> errs = this.getPlayer().getInventory().addItem(stack);
        for(ItemStack is : errs.values())
            this.getPlayer().getWorld().dropItem(this.getPlayer().getLocation(), is);

        this.storedProduct -= limit;
        this.refreshInfoLabel();

        SmileyPlayerTrader.getInstance().getStatementHandler().run(
                StatementHandler.StatementType.CHANGE_STORED_PRODUCT,
                -limit, this.id
        );
    }

    private void refreshInfoLabel(){
        this.infoLabel.setName(I18N.translate("&eStored Product: %0%", this.storedProduct));
        this.refreshComponent(this.infoLabel);
    }

    private boolean onOkClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page, this.isMine));
        return false;
    }

    @Override
    protected boolean onPlayerInventoryClick(ClickType click, int clickSlot, ItemStack clickedStack) {
        if(clickedStack == null)
            return false;

        if(clickedStack.isSimilar(this.product)){
            int count = clickedStack.getAmount();

            int limit = SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageProductStorageLimit();
            if(limit != -1 && this.storedProduct + count > limit){
                GUIManager.sendErrorMessage(this.getPlayer(), I18N.translate("&cYou cannot store more than %0% of a product.", limit));
                return false;
            }

            // Add to storage
            this.storedProduct += count;
            SmileyPlayerTrader.getInstance().getStatementHandler().run(
                    StatementHandler.StatementType.CHANGE_STORED_PRODUCT,
                    count, this.id
            );

            // Remove from hand
            clickedStack.setAmount(clickedStack.getAmount() - count);

            this.refreshInfoLabel();
        }

        return false;
    }

    private boolean onWithdrawAllClick(ClickType clickType) {
        this.withdraw(Integer.MAX_VALUE);
        return false;
    }

    private boolean onWithdraw8Click(ClickType clickType) {
        this.withdraw(8);
        return false;
    }

    private boolean onWithdraw1Click(ClickType clickType) {
        this.withdraw(1);
        return false;
    }

}
