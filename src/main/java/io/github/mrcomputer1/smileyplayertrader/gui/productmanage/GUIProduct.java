package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SlotComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.lang.reflect.InvocationTargetException;

public class GUIProduct extends GUI {

    private final ProductState state;

    private final SlotComponent productSlot;

    public GUIProduct(Player uiPlayer, ProductState state) {
        super(
                state.isNew ? I18N.translate("&2Creating Product") : I18N.translate("&2Editing Product %0%", state.id),
                6
        );

        if(GeyserUtil.isBedrockPlayer(uiPlayer))
            this.setBackgroundFillItem(GUI.BACKGROUND_BEDROCK);

        this.state = state;

        this.setAllowInteractingWithPlayerInventory(true);

        //
        // Menu Bar
        //
        this.addChild(new ProductMenuBarComponent(0, this.state, ProductMenuBarComponent.EnumProductEditPage.PRODUCT_SETTINGS));

        //
        // Product Settings
        //

        // Product Slot
        this.addChild(new LabelComponent(
                4, 1,
                GeyserUtil.isBedrockPlayer(uiPlayer) ? Material.OAK_SIGN : Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                1,
                I18N.translate("&bInsert Product")
        ));

        this.productSlot = new SlotComponent(
                4, 2, () -> this.state.stack, this.state::setStack
        );
        this.productSlot.setCanModify(() -> {
            if(this.state.storedProduct > 0){
                this.getPlayer().sendMessage(I18N.translate("&cYou must withdraw all stored product before changing the product."));
                return false;
            }else return true;
        });
        this.addChild(this.productSlot);

        // Primary Cost
        ButtonComponent primaryCost = new ButtonComponent(
                3, 4, Material.GOLD_INGOT, 1,
                I18N.translate("&eSet Primary Cost")
        );
        primaryCost.setOnClickEvent(this::onPrimaryCostClick);
        this.addChild(primaryCost);

        // Secondary Cost
        ButtonComponent secondaryCost = new ButtonComponent(
                5, 4, Material.GOLD_INGOT, 2,
                I18N.translate("&eSet Secondary Cost (Not Required)")
        );
        secondaryCost.setOnClickEvent(this::onSecondaryCostClick);
        this.addChild(secondaryCost);

        //
        // Complete Buttons
        //

        // Create/Update btn
        ButtonComponent createUpdateBtn = new ButtonComponent(
                0, 5, Material.EMERALD_BLOCK, 1,
                this.state.isNew ? I18N.translate("&aCreate Product") : I18N.translate("&aUpdate Product")
        );
        createUpdateBtn.setOnClickEvent(this::onCreateOrUpdateClick);
        this.addChild(createUpdateBtn);

        // Cancel Create/Update btn
        ButtonComponent cancelBtn = new ButtonComponent(
                8, 5, Material.REDSTONE_BLOCK, 1,
                this.state.isNew ? I18N.translate("&cCancel Product Creation") : I18N.translate("&cCancel Product Update")
        );
        cancelBtn.setOnClickEvent(this::onCancelClick);
        this.addChild(cancelBtn);
    }

    private boolean onCancelClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.state.target, this.state.page, this.state.isMine));
        return false;
    }

    private boolean onCreateOrUpdateClick(ClickType type){
        if(this.productSlot.isChanged()){
            this.productSlot.updateItem(this.getPlayer(), true);
        }

        GUIProduct.onSave(this.getPlayer(), this.state);

        return false;
    }

    public static boolean onSave(Player player, ProductState state) {
        try {

            byte[] stackBytes =
                    (state.stack == null || state.stack.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(state.stack);
            byte[] costBytes =
                    (state.costStack == null || state.costStack.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(state.costStack);
            byte[] cost2Bytes =
                    (state.costStack2 == null || state.costStack2.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(state.costStack2);

            if(state.isNew){
                SmileyPlayerTrader.getInstance().getStatementHandler().run(
                        StatementHandler.StatementType.ADD_PRODUCT,
                        state.target.getUniqueId().toString(), stackBytes, costBytes, cost2Bytes,
                        true, true,
                        state.discount, state.priority, state.hideOnOutOfStock
                );
            }else{
                SmileyPlayerTrader.getInstance().getStatementHandler().run(
                        StatementHandler.StatementType.SET_PRODUCT_COST_COST2_SPECIALPRICE_PRIORITY_HIDEOUTOFSTOCK_PURCHASELIMIT,
                        stackBytes, costBytes, cost2Bytes,
                        state.discount, state.priority, state.hideOnOutOfStock, state.purchaseLimit,
                        state.id
                );
            }

            GUIManager.getInstance().openGui(player, new GUIProductList(player, state.target, state.page, state.isMine));

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean onPrimaryCostClick(ClickType clickType) {
        if(this.state.storedCost2 > 0){
            this.getPlayer().sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
            return false;
        }
        GUIManager.getInstance().openGui(this.getPlayer(), new GUISetCost(this.getPlayer(), this.state, true));
        return false;
    }

    private boolean onSecondaryCostClick(ClickType clickType) {
        if(this.state.storedCost > 0){
            this.getPlayer().sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
            return false;
        }
        GUIManager.getInstance().openGui(this.getPlayer(), new GUISetCost(this.getPlayer(), this.state, false));
        return false;
    }

}
