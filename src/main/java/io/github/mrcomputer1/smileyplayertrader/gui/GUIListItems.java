package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GUIListItems extends AbstractGUI {

    private Player player;
    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack MENU_BAR = AbstractGUI.createItem(Material.IRON_BARS, 1, ChatColor.RESET.toString());
    private static ItemStack NO_PREVIOUS_BTN = AbstractGUI.createItem(
            Material.RED_STAINED_GLASS_PANE, 1,
            I18N.translate("&cNo previous page.")
    );
    private static ItemStack CREATE_PRODUCT_BTN = AbstractGUI.createItem(
            Material.EMERALD, 1,
            I18N.translate("&aCreate New Product")
    );

    public static NamespacedKey IS_PRODUCT_BOOLEAN_KEY = new NamespacedKey(SmileyPlayerTrader.getInstance(), "is_product");

    public GUIListItems(int page){
        this.page = page;

        this.createInventory(I18N.translate("&2My Products (Page %0%)", page + 1), 6);

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 1, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 2, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 3, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 4, BORDER);
        GUIUtil.drawLine(this.getInventory(), 5 * 9, 3, MENU_BAR);
        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 6, 3, MENU_BAR);

        if(this.page == 0){
            this.getInventory().setItem((5 * 9) + 3, NO_PREVIOUS_BTN);
        }else {
            this.getInventory().setItem((5 * 9) + 3, createItem(
                    Material.ARROW, 1, I18N.translate("&aPrevious Page")
            ));
        }
        this.getInventory().setItem((5 * 9) + 5, createItem(
                Material.ARROW, 1, I18N.translate("&aNext Page")
        ));
        this.getInventory().setItem((5 * 9) + 4, CREATE_PRODUCT_BTN);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getCurrentItem() == null) {
            return true;
        }

        if(e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getPersistentDataContainer().getOrDefault(IS_PRODUCT_BOOLEAN_KEY, PersistentDataType.BYTE, (byte) 0) == 0) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(I18N.translate("&aNext Page"))) {
                GUIManager.getInstance().openGUI(player, new GUIListItems(this.page + 1));
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(I18N.translate("&aPrevious Page"))) {
                GUIManager.getInstance().openGUI(player, new GUIListItems(this.page - 1));
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(I18N.translate("&aCreate New Product"))) {
                GUIManager.getInstance().openGUI(player, new GUIProduct(new ProductGUIState(this.page)));
            }
        }else{
            if(e.getCurrentItem().getItemMeta().getLore() != null &&
                    e.getCurrentItem().getItemMeta().getLore().size() != 0 &&
                    e.getCurrentItem().getItemMeta().getLore().get(0).startsWith(I18N.translate("&eProduct ID: "))){
                int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(0).replace(I18N.translate("&eProduct ID: "), "").trim());
                if(e.getClick() == ClickType.LEFT){

                    // Left Click - Edit
                    ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id);
                    try {
                        if (set.next()) {
                            byte[] stackBytes = set.getBytes("product");
                            ItemStack stack = stackBytes == null ? null : MerchantUtil.buildItem(stackBytes);

                            byte[] cost1Bytes = set.getBytes("cost1");
                            ItemStack cost1 = cost1Bytes == null ? null : MerchantUtil.buildItem(cost1Bytes);

                            byte[] cost2Bytes = set.getBytes("cost2");
                            ItemStack cost2 = cost2Bytes == null ? null : MerchantUtil.buildItem(cost2Bytes);

                            int discount = set.getInt("special_price");
                            int priority = set.getInt("priority");

                            GUIManager.getInstance().openGUI(player, new GUIProduct(new ProductGUIState(
                                    this.page, id, stack, cost1, cost2, discount, priority
                            )));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                }else if(e.getClick() == ClickType.RIGHT){

                    // Right Click - Enable/Disable/Show/Hide
                    GUIManager.getInstance().openGUI(this.player, new GUIEnableDisableProduct(this.page, id));

                }else if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){

                    // Shift Click - Delete
                    GUIManager.getInstance().openGUI(this.player, new GUIDeleteProduct(this.page, id));

                }
            }
        }

        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void open(Player player) {
        this.player = player;

        List<ItemStack> stacks = new ArrayList<>();
        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.FIND_PRODUCTS_IN_PAGES, this.player.getUniqueId().toString(), 28, this.page * 28);
        try {
            while (set.next()) {
                byte[] product = set.getBytes("product");

                ItemStack is;
                if(product == null){
                    is = new ItemStack(Material.BARRIER);
                }else {
                    is = MerchantUtil.buildItem(product);
                }

                ItemMeta im = is.getItemMeta();
                im.getPersistentDataContainer().set(IS_PRODUCT_BOOLEAN_KEY, PersistentDataType.BYTE, (byte) 1);
                if(product == null)
                    im.setDisplayName(I18N.translate("&cProduct Not Set!"));

                List<String> lore = new ArrayList<>();
                lore.add(I18N.translate("&eProduct ID: ") + set.getInt("id"));
                lore.add(I18N.translate("&bClick to &lEdit"));

                if(set.getBoolean("enabled") && set.getBoolean("available")) {
                    lore.add(I18N.translate("&bRight Click to &lDisable/Hide"));
                }else{
                    lore.add(I18N.translate("&bRight Click to &lEnable/Show"));
                }

                lore.add(I18N.translate("&bShift Click to &lDelete"));

                im.setLore(lore);
                is.setItemMeta(im);
                stacks.add(is);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        GUIUtil.spreadItems(this.getInventory(), 1, 7, 1, 4, stacks.toArray(new ItemStack[0]));
    }

}
