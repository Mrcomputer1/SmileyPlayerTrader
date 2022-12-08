package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SlotComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class GUISetCost extends GUI {

    private final ProductState state;
    private final boolean isPrimary;

    private final SlotComponent priceSlot;

    public GUISetCost(ProductState state, boolean isPrimary) {
        super(isPrimary ? I18N.translate("&2Set Primary Cost") : I18N.translate("&2Set Secondary Cost"), 6);

        this.state = state;
        this.isPrimary = isPrimary;

        this.setAllowInteractingWithPlayerInventory(true);

        // Insert Label
        this.addChild(new LabelComponent(
                4, 1, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1,
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
        this.addChild(new PriceQuickSelectionComponent(1, 4, this.priceSlot, this.state, this.isPrimary));

        // Ok Button
        ButtonComponent okButton = new ButtonComponent(
                0, 5, Material.EMERALD_BLOCK, 1,
                I18N.translate("&aOK")
        );
        okButton.setOnClickEvent(this::onOkClick);
        this.addChild(okButton);
    }

    private boolean onOkClick(ClickType clickType) {
        if(this.priceSlot.isChanged())
            this.priceSlot.updateItem(this.getPlayer(), true);

        if(this.isPrimary){
            this.state.costStack = this.priceSlot.getItem();
        }else{
            this.state.costStack2 = this.priceSlot.getItem();
        }

        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProduct(this.state));

        return false;
    }

}
