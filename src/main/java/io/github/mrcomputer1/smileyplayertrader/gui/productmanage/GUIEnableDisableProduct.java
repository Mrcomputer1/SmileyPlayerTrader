package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;

public class GUIEnableDisableProduct extends GUI {

    private final int id;
    private final boolean enable;

    private final int page;
    private final OfflinePlayer target;
    private final boolean isMine;

    public GUIEnableDisableProduct(int id, boolean enable, int page, OfflinePlayer target, boolean isMine) {
        super(
                enable ? I18N.translate("&2Enable/Show Product %0%", id) : I18N.translate("&2Disable/Hide Product %0%", id),
                6
        );

        this.id = id;
        this.enable = enable;
        this.page = page;
        this.target = target;
        this.isMine = isMine;

        // Warning Label
        if(this.enable){
            this.addChild(new LabelComponent(
                    4, 2, Material.RED_STAINED_GLASS_PANE, 1,
                    I18N.translate("&cEnabling/showing product will make it purchasable!")
            ));
        }else{
            this.addChild(new LabelComponent(
                    4, 2, Material.RED_STAINED_GLASS_PANE, 1,
                    I18N.translate("&cDisabling/hiding product will make it no longer purchasable!")
            ));
        }

        if(this.enable){
            // Enable Button
            ButtonComponent enableBtn = new ButtonComponent(
                    3, 4, Material.EMERALD_BLOCK, 1,
                    I18N.translate("&aEnable/Show Product")
            );
            enableBtn.setOnClickEvent(this::onEnableClick);
            this.addChild(enableBtn);

            // Don't Enable Button
            ButtonComponent dontEnableBtn = new ButtonComponent(
                    5, 4, Material.REDSTONE_BLOCK, 1,
                    I18N.translate("&cDon't Enable/Show Product")
            );
            dontEnableBtn.setOnClickEvent(this::onCancelClick);
            this.addChild(dontEnableBtn);
        }else{
            // Hide Button
            ButtonComponent hideBtn = new ButtonComponent(
                    2, 4, Material.EMERALD_BLOCK, 1,
                    I18N.translate("&aHide Product")
            );
            hideBtn.setOnClickEvent(this::onHideClick);
            this.addChild(hideBtn);

            // Disable Button
            ButtonComponent disableBtn = new ButtonComponent(
                    4, 4, Material.GOLD_BLOCK, 1,
                    I18N.translate("&aDisable Product")
            );
            disableBtn.setOnClickEvent(this::onDisableClick);
            this.addChild(disableBtn);

            // Don't Disable/Hide Button
            ButtonComponent dontDisableHideBtn = new ButtonComponent(
                    6, 4, Material.REDSTONE_BLOCK, 1,
                    I18N.translate("&cDon't Enable/Show Product")
            );
            dontDisableHideBtn.setOnClickEvent(this::onCancelClick);
            this.addChild(dontDisableHideBtn);
        }
    }

    private boolean onDisableClick(ClickType clickType) {
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DISABLE_PRODUCT, this.id);
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page, this.isMine));
        return false;
    }

    private boolean onHideClick(ClickType clickType) {
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.HIDE_PRODUCT, this.id);
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page, this.isMine));
        return false;
    }

    private boolean onCancelClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page, this.isMine));
        return false;
    }

    private boolean onEnableClick(ClickType clickType) {
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.ENABLE_PRODUCT, this.id);
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.getPlayer(), this.target, this.page, this.isMine));
        return false;
    }

}
