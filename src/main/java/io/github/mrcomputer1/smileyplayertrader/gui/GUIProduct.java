package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ReflectionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class GUIProduct extends AbstractGUI {
    private boolean isEditing;
    private long productId;
    private ItemStack stack = null;
    private ItemStack costStack = null;
    private ItemStack costStack2 = null;
    private Player player;

    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INSERT_PRODUCT_LBL = AbstractGUI.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&bInsert Product"));
    private static ItemStack COST_PRIMARY_BTN = AbstractGUI.createItem(Material.GOLD_INGOT, 1, I18N.translate("&eSet Primary Cost"));
    private static ItemStack COST_SECONDARY_BTN = AbstractGUI.createItem(Material.GOLD_INGOT, 2, I18N.translate("&eSet Secondary Cost (Not Required)"));

    private static ItemStack CREATE_PRODUCT = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aCreate Product"));
    private static ItemStack CANCEL_CREATE_PRODUCT = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cCancel Product Creation"));
    private static ItemStack UPDATE_PRODUCT = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aUpdate Product"));
    private static ItemStack CANCEL_UPDATE_PRODUCT = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cCancel Product Update"));

    public GUIProduct(int page, boolean isEditing, ItemStack stack, long productId, ItemStack costStack, ItemStack costStack2){
        this.isEditing = isEditing;
        this.productId = productId;
        this.stack = stack;
        this.costStack = costStack;
        this.costStack2 = costStack2;
        if(this.stack == null){
            this.stack = new ItemStack(Material.AIR);
        }
        if(this.costStack == null){
            this.costStack = new ItemStack(Material.AIR);
        }
        if(this.costStack2 == null){
            this.costStack2 = new ItemStack(Material.AIR);
        }
        this.page = page;

        if(isEditing) {
            this.createInventory(I18N.translate("&2Editing Product %0%", productId), 6);
            this.getInventory().setItem(5 * 9, UPDATE_PRODUCT.clone());
            this.getInventory().setItem((5 * 9) + 8, CANCEL_UPDATE_PRODUCT.clone());
        }else{
            this.createInventory(I18N.translate("&2Creating Product"), 6);
            this.getInventory().setItem(5 * 9, CREATE_PRODUCT.clone());
            this.getInventory().setItem((5 * 9) + 8, CANCEL_CREATE_PRODUCT.clone());
        }

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);

        GUIUtil.drawLine(this.getInventory(), 1 * 9, 4, BORDER);
        this.getInventory().setItem((1 * 9) + 4, INSERT_PRODUCT_LBL.clone());
        GUIUtil.drawLine(this.getInventory(), (1 * 9) + 5, 4, BORDER);

        GUIUtil.drawLine(this.getInventory(), 2 * 9, 4, BORDER);
        this.getInventory().setItem((2 * 9) + 4, this.stack.clone());
        GUIUtil.drawLine(this.getInventory(), (2 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 3, BORDER);

        GUIUtil.drawLine(this.getInventory(), 4 * 9, 3, BORDER);
        this.getInventory().setItem((4 * 9) + 3, COST_PRIMARY_BTN.clone());
        this.getInventory().setItem((4 * 9) + 4, BORDER.clone());
        this.getInventory().setItem((4 * 9) + 5, COST_SECONDARY_BTN.clone());
        GUIUtil.drawLine(this.getInventory(), (4 * 9) + 6, 3, BORDER);

        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 1, 7, BORDER);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){ // Cancel double clicks and shift clicks
            return true;
        }
        if(e.getRawSlot() == InventoryView.OUTSIDE){ // Disallow dropping items out of inventory
            return true;
        }

        if(e.getRawSlot() == 22){

            // Input Slot
            if(!this.stack.getType().isAir() && this.stack.equals(e.getCurrentItem())){
                this.getInventory().setItem(22 , null);
                this.stack = new ItemStack(Material.AIR);
                return true;
            }
            return false;

        }else if(e.getView().getInventory(e.getRawSlot()).getType() == InventoryType.PLAYER){

            // Player Inventory Area
            return false;

        }else if(e.getCurrentItem() == null) {
            return true;
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aCreate Product")) ||
                e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aUpdate Product"))){

            // Create/Update Product
            if(this.getInventory().getItem(22) != null && !this.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }

            try {
                this.stack = this.getInventory().getItem(22);

                byte[] stackBytes = (this.stack == null || this.stack.getType().isAir()) ? null : ReflectionUtil.itemStackToByteArray(this.stack);
                byte[] costBytes = (this.costStack == null || this.costStack.getType().isAir()) ? null : ReflectionUtil.itemStackToByteArray(this.costStack);
                byte[] cost2Bytes = (this.costStack2 == null || this.costStack2.getType().isAir()) ? null : ReflectionUtil.itemStackToByteArray(this.costStack2);

                if (isEditing) {
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_PRODUCT_COST_COST2,
                            stackBytes, costBytes, cost2Bytes, this.productId);
                } else {
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.ADD_PRODUCT,
                            this.player.getUniqueId().toString(), stackBytes, costBytes, cost2Bytes, true, true);
                }

                GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cCancel Product Creation")) ||
                e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cCancel Product Update"))){

            // Cancel Product Create/Update
            if(this.getInventory().getItem(22) != null && !this.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Primary Cost"))){

            // Set Primary Cost
            if(this.getInventory().getItem(22) != null && !this.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(this.page, true, isEditing, productId, stack, costStack, costStack2));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Secondary Cost (Not Required)"))){

            // Set Secondary Cost
            if(this.getInventory().getItem(22) != null && !this.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(this.page, false, isEditing, productId, stack, costStack, costStack2));

        }
        return true;
    }

    @Override
    public void close() {
        if(this.getInventory().getItem(22) != null && !this.stack.equals(this.getInventory().getItem(22))){
            this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
        }
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
