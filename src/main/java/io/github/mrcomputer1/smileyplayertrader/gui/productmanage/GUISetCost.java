package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.gui.bedrock.BedrockGUISetCostQuantity;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SlotComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GUISetCost extends GUI {

    private final ProductState state;
    private final boolean isPrimary;

    private final SlotComponent priceSlot;

    public GUISetCost(Player uiPlayer, ProductState state, boolean isPrimary) {
        super(isPrimary ? I18N.translate("&2Set Primary Cost") : I18N.translate("&2Set Secondary Cost"), 6);

        if(GeyserUtil.isBedrockPlayer(uiPlayer))
            this.setBackgroundFillItem(GUI.BACKGROUND_BEDROCK);

        this.state = state;
        this.isPrimary = isPrimary;

        this.setAllowInteractingWithPlayerInventory(true);

        // Insert Label
        this.addChild(new LabelComponent(
                4, 1,
                GeyserUtil.isBedrockPlayer(uiPlayer) ? Material.OAK_SIGN : Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                1,
                I18N.translate("&eInsert Price")
        ));

        // Insert Slot
        this.priceSlot = new SlotComponent(
                4, 2,
                () -> this.isPrimary ? this.state.costStack : this.state.costStack2,
                (stack) -> {
                    if(this.isPrimary)
                        this.state.setCostStack(stack);
                    else
                        this.state.setCostStack2(stack);
                }
        );
        this.addChild(this.priceSlot);

        // Quick Selection Row
        this.addChild(new PriceQuickSelectionComponent(uiPlayer, 1, 4, this.priceSlot, this.state, this.isPrimary));

        // Ok Button
        ButtonComponent okButton = new ButtonComponent(
                0, 5, Material.EMERALD_BLOCK, 1,
                I18N.translate("&aOK")
        );
        okButton.setOnClickEvent(this::onOkClick);
        this.addChild(okButton);

        // Bedrock Only: Adjust Quantity Button
        if(GeyserUtil.isBedrockPlayer(uiPlayer)) {
            ButtonComponent adjustAmount = new ButtonComponent(
                    8, 5, Material.GOLD_BLOCK, 1,
                    I18N.translate("&eAdjust Quantity")
            );
            adjustAmount.setOnClickEvent(this::onAdjustAmountClick);
            this.addChild(adjustAmount);
        }
    }

    private boolean onOkClick(ClickType clickType) {
        if(this.priceSlot.isChanged())
            this.priceSlot.updateItem(this.getPlayer(), true);

        if(this.isPrimary){
            this.state.setCostStack(this.priceSlot.getItem());
        }else{
            this.state.setCostStack2(this.priceSlot.getItem());
        }

        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProduct(this.getPlayer(), this.state));

        return false;
    }

    private boolean onAdjustAmountClick(ClickType clickType){
        if(this.priceSlot.getItem() == null || this.priceSlot.getItem().getType().isAir())
            return false;

        if(this.priceSlot.isChanged())
            this.priceSlot.updateItem(this.getPlayer(), true);

        if(this.isPrimary){
            this.state.setCostStack(this.priceSlot.getItem());
        }else{
            this.state.setCostStack2(this.priceSlot.getItem());
        }

        this.getPlayer().closeInventory();
        GeyserUtil.showFormDelayed(this.getPlayer(), new BedrockGUISetCostQuantity(this.getPlayer(), this.state, this.isPrimary));

        return false;
    }

}
