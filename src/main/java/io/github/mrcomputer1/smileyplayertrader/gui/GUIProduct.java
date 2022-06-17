package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
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
    private final ProductGUIState state;
    private Player player;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INSERT_PRODUCT_LBL = AbstractGUI.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&bInsert Product"));
    private static ItemStack COST_PRIMARY_BTN = AbstractGUI.createItem(Material.GOLD_INGOT, 1, I18N.translate("&eSet Primary Cost"));
    private static ItemStack COST_SECONDARY_BTN = AbstractGUI.createItem(Material.GOLD_INGOT, 2, I18N.translate("&eSet Secondary Cost (Not Required)"));
    private static ItemStack DISCOUNT_BTN = AbstractGUI.createItem(Material.IRON_INGOT, 1, I18N.translate("&eSet Optional Discount"));
    private static ItemStack PRIORITY_BTN = AbstractGUI.createItemWithLore(
            Material.NETHER_STAR, 1,
            I18N.translate("&eSet Priority"),
            I18N.translate("&eHigher priorities appear higher in the trade list.")
    );

    private static ItemStack CREATE_PRODUCT = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aCreate Product"));
    private static ItemStack CANCEL_CREATE_PRODUCT = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cCancel Product Creation"));
    private static ItemStack UPDATE_PRODUCT = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aUpdate Product"));
    private static ItemStack CANCEL_UPDATE_PRODUCT = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cCancel Product Update"));

    public GUIProduct(ProductGUIState state){
        this.state = state;

        if(this.state.isEditing) {
            this.createInventory(I18N.translate("&2Editing Product %0%", this.state.id), 6);
            this.getInventory().setItem(5 * 9, UPDATE_PRODUCT.clone());
            this.getInventory().setItem((5 * 9) + 8, CANCEL_UPDATE_PRODUCT.clone());
        }else{
            this.createInventory(I18N.translate("&2Creating Product"), 6);
            this.getInventory().setItem(5 * 9, CREATE_PRODUCT.clone());
            this.getInventory().setItem((5 * 9) + 8, CANCEL_CREATE_PRODUCT.clone());
        }

        GUIUtil.drawLine(this.getInventory(), (0 * 9), 7, BORDER);
        this.getInventory().setItem((0 * 9) + 7, PRIORITY_BTN.clone());
        this.getInventory().setItem((0 * 9) + 8, DISCOUNT_BTN.clone());

        GUIUtil.drawLine(this.getInventory(), 1 * 9, 4, BORDER);
        this.getInventory().setItem((1 * 9) + 4, INSERT_PRODUCT_LBL.clone());
        GUIUtil.drawLine(this.getInventory(), (1 * 9) + 5, 4, BORDER);

        GUIUtil.drawLine(this.getInventory(), 2 * 9, 4, BORDER);
        this.getInventory().setItem((2 * 9) + 4, this.state.stack.clone());
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
            if(this.state.storedProduct > 0){
                this.player.sendMessage(I18N.translate("&cYou must withdraw all stored product before changing the product."));
                return true;
            }

            if(!this.state.stack.getType().isAir() && this.state.stack.equals(e.getCurrentItem())){
                this.getInventory().setItem(22 , null);
                this.state.stack = new ItemStack(Material.AIR);
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
            if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }

            try {
                this.state.stack = this.getInventory().getItem(22);

                byte[] stackBytes = (this.state.stack == null || this.state.stack.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(this.state.stack);
                byte[] costBytes = (this.state.costStack == null || this.state.costStack.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(this.state.costStack);
                byte[] cost2Bytes = (this.state.costStack2 == null || this.state.costStack2.getType().isAir()) ? null : VersionSupport.itemStackToByteArray(this.state.costStack2);

                if (this.state.isEditing) {
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_PRODUCT_COST_COST2_SPECIALPRICE_PRIORITY,
                            stackBytes, costBytes, cost2Bytes, this.state.discount, this.state.priority, this.state.id);
                } else {
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.ADD_PRODUCT,
                            this.player.getUniqueId().toString(), stackBytes, costBytes, cost2Bytes, true, true, 0);
                }

                GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.state.page));
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cCancel Product Creation")) ||
                e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cCancel Product Update"))){
            GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.state.page));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Primary Cost"))){

            // Set Primary Cost
            if(this.state.storedCost > 0){
                this.player.sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
                return true;
            }

            if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.state.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(true, state));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Secondary Cost (Not Required)"))){

            // Set Secondary Cost
            if(this.state.storedCost > 0){
                this.player.sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
                return true;
            }

            if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.state.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(false, state));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Optional Discount"))){

            // Set Discount
            if(this.state.storedCost > 0){
                this.player.sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
                return true;
            }

            if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.state.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUIDiscount(state));

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eSet Priority"))){

            // Set Priority
            if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
            this.state.stack = this.getInventory().getItem(22);
            GUIManager.getInstance().openGUI(this.player, new GUIPriority(state));

        }
        return true;
    }

    @Override
    public void close() {
        if(this.getInventory().getItem(22) != null && !this.state.stack.equals(this.getInventory().getItem(22))){
            this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
        }
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
