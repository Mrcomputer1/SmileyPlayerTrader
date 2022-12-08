package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SkullButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class GUIPriority extends GUI {

    private final ProductState state;

    private final LabelComponent priorityLabel;
    private final SkullButtonComponent upButton;
    private final SkullButtonComponent downButton;

    public GUIPriority(ProductState state) {
        super(I18N.translate("&2Set Priority"), 6);

        this.state = state;

        // Menu Bar
        this.addChild(new ProductMenuBarComponent(0, this.state, ProductMenuBarComponent.EnumProductEditPage.PRIORITY));

        // Label
        this.priorityLabel = new LabelComponent(
                4, 2, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1,
                I18N.translate("&bPriority: %0%", this.state.priority),
                I18N.translate("&eHigher priorities appear higher in the trade list.")
        );
        this.addChild(this.priorityLabel);

        // Up/Down Buttons
        String[] lore = new String[]{
                I18N.translate("&bPriority: %0%", this.state.priority),
                I18N.translate("&bLeft click for &l1"),
                I18N.translate("&bRight click for &l10")
        };

        // Up
        this.upButton = new SkullButtonComponent(
                3, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowUp",
                I18N.translate("&aIncrease Priority"),
                lore
        );
        this.upButton.setOnClickEvent(this::onUp);
        this.addChild(this.upButton);

        // Down
        this.downButton = new SkullButtonComponent(
                5, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowDown",
                I18N.translate("&cDecrease Priority"),
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
                I18N.translate("&cReset Priority")
        );
        resetButton.setOnClickEvent(this::onReset);
        this.addChild(resetButton);
    }

    private void refreshText(){
        this.priorityLabel.setName(I18N.translate("&bPriority: %0%", this.state.priority));
        this.upButton.getLore().set(0, I18N.translate("&bPriority: %0%", this.state.priority));
        this.downButton.getLore().set(0, I18N.translate("&bPriority: %0%", this.state.priority));

        this.refreshComponent(this.priorityLabel);
        this.refreshComponent(this.upButton);
        this.refreshComponent(this.downButton);
    }

    private boolean onOk(ClickType clickType) {
        GUIProduct.onSave(this.getPlayer(), this.state);
        return false;
    }

    private boolean onReset(ClickType clickType) {
        this.state.priority = 0;
        this.refreshText();
        return false;
    }

    private boolean onUp(ClickType clickType) {
        this.state.priority += clickType == ClickType.RIGHT ? 10 : 1;
        this.refreshText();
        return false;
    }

    private boolean onDown(ClickType clickType) {
        int newPriority = this.state.priority - (clickType == ClickType.RIGHT ? 10 : 1);
        if(newPriority >= 0){
            this.state.priority = newPriority;
            this.refreshText();
        }
        return false;
    }


}
