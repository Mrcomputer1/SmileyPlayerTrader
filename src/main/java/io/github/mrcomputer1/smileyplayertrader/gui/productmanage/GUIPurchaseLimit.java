package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SkullButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class GUIPurchaseLimit extends GUI {

    private final ProductState state;

    private final LabelComponent limitLabel;
    private final SkullButtonComponent upButton;
    private final SkullButtonComponent downButton;

    public GUIPurchaseLimit(ProductState state) {
        super(I18N.translate("&2Set Purchase Limit"), 6);

        this.state = state;

        // Menu Bar
        this.addChild(new ProductMenuBarComponent(0, this.state, ProductMenuBarComponent.EnumProductEditPage.PURCHASE_LIMIT));

        // Label
        this.limitLabel = new LabelComponent(
                4, 2, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1,
                I18N.translate("&bPurchase Limit: %0%", this.state.purchaseLimit == -1 ? I18N.translate("&cDisabled") : this.state.purchaseLimit)
        );
        this.addChild(this.limitLabel);

        // Up/Down Buttons
        String[] lore = new String[]{
                I18N.translate("&bPurchase Limit: %0%", this.state.purchaseLimit == -1 ? I18N.translate("&cDisabled") : this.state.purchaseLimit),
                I18N.translate("&bLeft click for &l1"),
                I18N.translate("&bRight click for &l10")
        };

        // Up
        this.upButton = new SkullButtonComponent(
                3, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowUp",
                I18N.translate("&aIncrease Purchase Limit"),
                lore
        );
        this.upButton.setOnClickEvent(this::onUp);
        this.addChild(this.upButton);

        // Down
        this.downButton = new SkullButtonComponent(
                5, 4, Material.PLAYER_HEAD, 1, "MHF_ArrowDown",
                I18N.translate("&cDecrease Purchase Limit"),
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
                I18N.translate("&cReset Purchase Limit to Disabled")
        );
        resetButton.setOnClickEvent(this::onReset);
        this.addChild(resetButton);

        // Purchase Count Reset
        ButtonComponent countReset = new ButtonComponent(
                8, 1, Material.BARRIER, 1,
                I18N.translate("&cReset Purchase Count")
        );
        countReset.setOnClickEvent(this::onCountReset);
        this.addChild(countReset);
    }

    private void refreshText(){
        String msg = I18N.translate("&bPurchase Limit: %0%", this.state.purchaseLimit == -1 ? I18N.translate("&cDisabled") : this.state.purchaseLimit);
        this.limitLabel.setName(msg);
        this.upButton.getLore().set(0, msg);
        this.downButton.getLore().set(0, msg);

        this.refreshComponent(this.limitLabel);
        this.refreshComponent(this.upButton);
        this.refreshComponent(this.downButton);
    }

    private boolean onOk(ClickType clickType) {
        GUIProduct.onSave(this.getPlayer(), this.state);
        return false;
    }

    private boolean onReset(ClickType clickType) {
        this.state.purchaseLimit = -1;
        this.refreshText();
        return false;
    }

    private boolean onUp(ClickType clickType) {
        this.state.purchaseLimit += clickType == ClickType.RIGHT ? 10 : 1;
        this.refreshText();
        return false;
    }

    private boolean onDown(ClickType clickType) {
        int newPurchaseLimit = this.state.purchaseLimit - (clickType == ClickType.RIGHT ? 10 : 1);
        if(newPurchaseLimit >= -1){
            this.state.purchaseLimit = newPurchaseLimit;
            this.refreshText();
        }
        return false;
    }

    private boolean onCountReset(ClickType clickType){
        if(!this.state.isNew) {
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.RESET_PURCHASE_COUNT, this.state.id);
            this.getPlayer().sendMessage(I18N.translate("&aReset purchase count."));
        }
        return false;
    }

}
