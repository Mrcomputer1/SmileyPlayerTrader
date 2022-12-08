package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.BackgroundComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.LabelComponent;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;

public class GUIDeleteProduct extends GUI {

    private final int id;

    private final int page;
    private final OfflinePlayer target;
    private final boolean isMine;

    public GUIDeleteProduct(int id, int page, OfflinePlayer target, boolean isMine) {
        super(I18N.translate("&2Delete Product %0%", id), 6);

        this.id = id;
        this.page = page;
        this.target = target;
        this.isMine = isMine;

        // Background
        this.addChild(new BackgroundComponent(
                0, 0, 9, this.getRows(),
                Material.RED_STAINED_GLASS_PANE, 1, I18N.translate("&4&k&lAAA &c&lWARNING &4&k&lAAA")
        ));

        // Warning Label
        this.addChild(new LabelComponent(
                4, 2, Material.YELLOW_STAINED_GLASS_PANE, 1,
                I18N.translate("&eYou are about to delete a product!")
        ));

        // Delete Button
        ButtonComponent deleteBtn = new ButtonComponent(
                3, 4, Material.EMERALD_BLOCK, 1,
                I18N.translate("&cDelete Product")
        );
        deleteBtn.setOnClickEvent(this::onDeleteClick);
        this.addChild(deleteBtn);

        // Don't Delete Button
        ButtonComponent dontDeleteBtn = new ButtonComponent(
                5, 4, Material.REDSTONE_BLOCK, 1,
                I18N.translate("&aDon't Delete Product")
        );
        dontDeleteBtn.setOnClickEvent(this::onDontDeleteClick);
        this.addChild(dontDeleteBtn);
    }

    private boolean onDontDeleteClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.target, this.page, this.isMine));
        return false;
    }

    private boolean onDeleteClick(ClickType clickType) {
        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DELETE_PRODUCT, this.id);
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIProductList(this.target, this.page, this.isMine));
        return false;
    }

}
