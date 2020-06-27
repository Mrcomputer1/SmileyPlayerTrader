package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIDeleteProduct extends AbstractGUI {
    private Player player;
    private long productId;

    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.RED_STAINED_GLASS_PANE, 1, I18N.translate("&4&k&lAAA &c&lWARNING &4&k&lAAA"));
    private static ItemStack WARN = AbstractGUI.createItem(Material.YELLOW_STAINED_GLASS_PANE, 1, I18N.translate("&eYou are about to delete a product!"));
    private static ItemStack DELETE_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&cDelete Product"));
    private static ItemStack CANCEL_DELETE_BTN = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&aDon't Delete Product"));

    public GUIDeleteProduct(int page, long productId){
        this.productId = productId;
        this.page = page;

        this.createInventory(I18N.translate("&2Delete Product %0%", this.productId), 6);

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);
        GUIUtil.fillRow(this.getInventory(), 1, BORDER);

        GUIUtil.drawLine(this.getInventory(), (2 * 9), 4, BORDER);
        this.getInventory().setItem((2 * 9) + 4, WARN.clone());
        GUIUtil.drawLine(this.getInventory(), (2 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 3, BORDER);

        GUIUtil.drawLine(this.getInventory(), (4 * 9), 3, BORDER);
        this.getInventory().setItem((4 * 9) + 3, DELETE_BTN.clone());
        this.getInventory().setItem((4 * 9) + 4, BORDER.clone());
        this.getInventory().setItem((4 * 9) + 5, CANCEL_DELETE_BTN.clone());
        GUIUtil.drawLine(this.getInventory(), (4 * 9) + 6, 3, BORDER);

        GUIUtil.fillRow(this.getInventory(), 5, BORDER);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getCurrentItem() == null){
            return true;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cDelete Product"))){
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DELETE_PRODUCT, this.productId);
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aDon't Delete Product"))){
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));
        }
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
