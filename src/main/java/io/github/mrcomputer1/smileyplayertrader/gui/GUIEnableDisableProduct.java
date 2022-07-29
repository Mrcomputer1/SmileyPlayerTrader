package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GUIEnableDisableProduct extends AbstractGUI {
    private Player player;
    private long productId;
    private boolean enable = false;

    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack WARN_DISABLE = AbstractGUI.createItem(Material.RED_STAINED_GLASS_PANE, 1, I18N.translate("&cDisabling/hiding product will make it no longer purchasable!"));
    private static ItemStack WARN_ENABLE = AbstractGUI.createItem(Material.RED_STAINED_GLASS_PANE, 1, I18N.translate("&cEnabling/showing product will make it purchasable!"));
    private static ItemStack HIDE_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aHide Product"));
    private static ItemStack CANCEL_DISABLE_BTN = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cDon't Disable/Hide Product"));
    private static ItemStack DISABLE_BTN = AbstractGUI.createItem(Material.GOLD_BLOCK, 1, I18N.translate("&aDisable Product"));
    private static ItemStack ENABLE_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aEnable/Show Product"));
    private static ItemStack CANCEL_ENABLE_BTN = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cDon't Enable/Show Product"));

    public GUIEnableDisableProduct(int page, long productId){
        this.productId = productId;
        this.page = page;

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_ENABLED, productId)) {
            if (set.next()) {
                this.enable = !set.getBoolean("enabled") || !set.getBoolean("available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(enable) {
            this.createInventory(I18N.translate("&2Enable/Show Product %0%", this.productId), 6);
        }else{
            this.createInventory(I18N.translate("&2Disable/Hide Product %0%", this.productId), 6);
        }

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);
        GUIUtil.fillRow(this.getInventory(), 1, BORDER);

        GUIUtil.drawLine(this.getInventory(), (2 * 9), 4, BORDER);
        if(enable) {
            this.getInventory().setItem((2 * 9) + 4, WARN_ENABLE.clone());
        }else{
            this.getInventory().setItem((2 * 9) + 4, WARN_DISABLE.clone());
        }
        GUIUtil.drawLine(this.getInventory(), (2 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 3, BORDER);

        GUIUtil.drawLine(this.getInventory(), (4 * 9), 9, BORDER);
        if(enable){
            this.getInventory().setItem((4 * 9) + 3, ENABLE_BTN.clone());
            this.getInventory().setItem((4 * 9) + 4, BORDER.clone());
            this.getInventory().setItem((4 * 9) + 5, CANCEL_ENABLE_BTN.clone());
        }else{
            this.getInventory().setItem((4 * 9) + 2, HIDE_BTN.clone());
            this.getInventory().setItem((4 * 9) + 3, BORDER.clone());
            this.getInventory().setItem((4 * 9) + 4, DISABLE_BTN.clone());
            this.getInventory().setItem((4 * 9) + 5, BORDER.clone());
            this.getInventory().setItem((4 * 9) + 6, CANCEL_DISABLE_BTN.clone());
        }

        GUIUtil.fillRow(this.getInventory(), 5, BORDER);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getCurrentItem() == null){
            return true;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aDisable Product")) ||
            e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aEnable/Show Product"))){
            if(enable){
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.ENABLE_PRODUCT, this.productId);
            }else {
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DISABLE_PRODUCT, this.productId);
            }
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aHide Product"))){
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.HIDE_PRODUCT, this.productId);
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cDon't Disable/Hide Product")) ||
            e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cDon't Enable/Show Product"))){
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
