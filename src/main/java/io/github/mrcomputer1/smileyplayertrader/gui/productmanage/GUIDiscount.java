package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SkullButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class GUIDiscount extends GUI {

    private final ProductState state;

    private final LabelComponent discountLabel;
    private final SkullButtonComponent upButton;
    private final SkullButtonComponent downButton;

    public GUIDiscount(ProductState state) {
        super(I18N.translate("&2Set Discount"), 6);

        this.state = state;

        // Menu Bar
        this.addChild(new ProductMenuBarComponent(0, this.state, ProductMenuBarComponent.EnumProductEditPage.DISCOUNT));

        // Label
        this.discountLabel = new LabelComponent(
            4, 2, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1,
                I18N.translate("&bDiscount: %0%", this.state.discount),
                I18N.translate("&eNegative numbers will increase the price.")
        );
        this.addChild(this.discountLabel);

        // Up/Down Buttons
        String[] lore = new String[]{
                I18N.translate("&bDiscount: %0%", this.state.discount),
                I18N.translate("&bLeft click for &l1"),
                I18N.translate("&bRight click for &l10")
        };

        // Up
        this.upButton = new SkullButtonComponent(
                3, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowUp",
                I18N.translate("&aIncrease Discount"),
                lore
        );
        this.upButton.setOnClickEvent(this::onUp);
        this.addChild(this.upButton);

        // Down
        this.downButton = new SkullButtonComponent(
                5, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowDown",
                I18N.translate("&cDecrease Discount"),
                lore
        );
        this.downButton.setOnClickEvent(this::onDown);
        this.addChild(this.downButton);

        // OK
        ButtonComponent okButton = new ButtonComponent(
                0, 5, Material.EMERALD_BLOCK, 1,
                I18N.translate("&aOK")
        );
        okButton.setOnClickEvent(this::onOk);
        this.addChild(okButton);

        // Reset
        ButtonComponent resetButton = new ButtonComponent(
                8, 5, Material.REDSTONE_BLOCK, 1,
                I18N.translate("&cReset Discount")
        );
        resetButton.setOnClickEvent(this::onReset);
        this.addChild(resetButton);
    }

    private void refreshText(){
        this.discountLabel.setName(I18N.translate("&bDiscount: %0%", this.state.discount));
        this.upButton.getLore().set(0, I18N.translate("&bDiscount: %0%", this.state.discount));
        this.downButton.getLore().set(0, I18N.translate("&bDiscount: %0%", this.state.discount));

        this.refreshComponent(this.discountLabel);
        this.refreshComponent(this.upButton);
        this.refreshComponent(this.downButton);
    }

    private boolean onOk(ClickType clickType) {
        GUIProduct.onSave(this.getPlayer(), this.state);
        return false;
    }

    private boolean onReset(ClickType clickType) {
        this.state.discount = 0;
        this.refreshText();
        return false;
    }

    private boolean onUp(ClickType clickType) {
        int newDiscount = this.state.discount + (clickType == ClickType.RIGHT ? 10 : 1);
        int testValue = -newDiscount + this.state.costStack.getAmount();
        if(testValue >= 1 && testValue <= this.state.costStack.getMaxStackSize()){
            this.state.discount = newDiscount;
            this.refreshText();
        }
        return false;
    }

    private boolean onDown(ClickType clickType) {
        int newDiscount = this.state.discount - (clickType == ClickType.RIGHT ? 10 : 1);
        int testValue = -newDiscount + this.state.costStack.getAmount();
        if(testValue >= 1 && testValue <= this.state.costStack.getMaxStackSize()){
            this.state.discount = newDiscount;
            this.refreshText();
        }
        return false;
    }


}
