package io.github.mrcomputer1.smileyplayertrader.gui;

import com.google.common.primitives.Ints;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class GUISetCost extends AbstractGUI {
    private Player player;
    private boolean primary;
    private final ProductGUIState state;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INSERT_LBL = AbstractGUI.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&eInsert Price"));
    private static ItemStack OK_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aOK"));
    private static ItemStack MORE_ITEMS_BTN = AbstractGUI.createItem(Material.BEACON, 1, I18N.translate("&bMore Items..."));

    static{
        ItemMeta im = MORE_ITEMS_BTN.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.setLore(Arrays.asList(
                I18N.translate("&eShift Click to increase value"),
                I18N.translate("&eShift Right Click to decrease value")
        ));
        MORE_ITEMS_BTN.setItemMeta(im);
    }

    private static NamespacedKey IS_QUICK_SELECT = new NamespacedKey(SmileyPlayerTrader.getInstance(), "is_quick_select");

    public GUISetCost(boolean primary, ProductGUIState state){
        this.primary = primary;
        this.state = state;

        if(primary) {
            this.createInventory(I18N.translate("&2Set Primary Cost"), 6);
        }else{
            this.createInventory(I18N.translate("&2Set Secondary Cost"), 6);
        }

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);

        GUIUtil.drawLine(this.getInventory(), 9, 4, BORDER);
        this.getInventory().setItem(9 + 4, INSERT_LBL.clone());
        GUIUtil.drawLine(this.getInventory(), 9 + 5, 4, BORDER);

        GUIUtil.drawLine(this.getInventory(), (2 * 9), 4, BORDER);
        if(primary){
            this.getInventory().setItem((2 * 9) + 4, this.state.costStack.clone());
        }else{
            this.getInventory().setItem((2 * 9) + 4, this.state.costStack2.clone());
        }
        GUIUtil.drawLine(this.getInventory(), (2 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 3, BORDER);

        // Quick Selection Row
        List<LinkedHashMap<String, Object>> priceQuickSelection = (List<LinkedHashMap<String, Object>>) SmileyPlayerTrader.getInstance().getConfig().getList("priceQuickSelection", new ArrayList<>());
        List<ItemStack> stacks = new ArrayList<>();
        int stackCount = 0;
        for(LinkedHashMap<String, Object> item : priceQuickSelection){
            if(stackCount++ >= 6){ // ensure only 6 items are on the quick selection row
                SmileyPlayerTrader.getInstance().getLogger().warning("You have too many quick selection items.");
                break;
            }
            ItemStack stack = createQuickSelectionRowItem(item);
            if(stack != null)
                stacks.add(stack);
        }
        stacks.add(MORE_ITEMS_BTN.clone());
        createQuickSelectionRow(stacks);
        // End Quick Selection Row

        this.getInventory().setItem(5 * 9, OK_BTN.clone());
        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 1, 8, BORDER);
    }

    // Quick Selection Row
    private void createQuickSelectionRow(List<ItemStack> stacks) {
        // probably a better way to write this, but this works.
        switch(stacks.size()){
            case 1:
                this.getInventory().setItem((4 * 9) + 4, stacks.get(0));
                break;
            case 2:
                this.getInventory().setItem((4 * 9) + 3, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 5, stacks.get(1));
                break;
            case 3:
                this.getInventory().setItem((4 * 9) + 2, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 4, stacks.get(1));
                this.getInventory().setItem((4 * 9) + 6, stacks.get(2));
                break;
            case 4:
                this.getInventory().setItem((4 * 9) + 1, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 3, stacks.get(1));
                this.getInventory().setItem((4 * 9) + 5, stacks.get(2));
                this.getInventory().setItem((4 * 9) + 7, stacks.get(3));
                break;
            case 5:
                this.getInventory().setItem((4 * 9) + 2, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 3, stacks.get(1));
                this.getInventory().setItem((4 * 9) + 4, stacks.get(2));
                this.getInventory().setItem((4 * 9) + 5, stacks.get(3));
                this.getInventory().setItem((4 * 9) + 6, stacks.get(4));
                break;
            case 6:
                this.getInventory().setItem((4 * 9) + 1, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 2, stacks.get(1));
                this.getInventory().setItem((4 * 9) + 3, stacks.get(2));
                this.getInventory().setItem((4 * 9) + 4, stacks.get(3));
                this.getInventory().setItem((4 * 9) + 5, stacks.get(4));
                this.getInventory().setItem((4 * 9) + 7, stacks.get(5));
                break;
            case 7:
                this.getInventory().setItem((4 * 9) + 1, stacks.get(0));
                this.getInventory().setItem((4 * 9) + 2, stacks.get(1));
                this.getInventory().setItem((4 * 9) + 3, stacks.get(2));
                this.getInventory().setItem((4 * 9) + 4, stacks.get(3));
                this.getInventory().setItem((4 * 9) + 5, stacks.get(4));
                this.getInventory().setItem((4 * 9) + 6, stacks.get(5));
                this.getInventory().setItem((4 * 9) + 7, stacks.get(6));
                break;
            default:
                throw new IllegalArgumentException("Unsupported amount of items");
        }

        for(int i = (4 * 9); i < (5 * 9); i++){
            if(this.getInventory().getItem(i) == null){
                this.getInventory().setItem(i, BORDER.clone());
            }
        }
    }

    private ItemStack createQuickSelectionRowItem(LinkedHashMap<String, Object> item){
        ItemStack is = ItemUtil.buildConfigurationItem(item);

        if(is == null)
            return null;

        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(IS_QUICK_SELECT, PersistentDataType.BYTE, (byte) 1);
        if(im.hasLore()) {
            List<String> lore = im.getLore();
            lore.add("");
            lore.add(I18N.translate("&eClick to increase value"));
            lore.add(I18N.translate("&eRight Click to decrease value"));
            im.setLore(lore);
        }else{
            im.setLore(Arrays.asList(
                    I18N.translate("&eClick to increase value"),
                    I18N.translate("&eRight Click to decrease value")
            ));
        }
        is.setItemMeta(im);

        return is;
    }

    private ItemStack getTrueItemStack(ItemStack itemStack){
        ItemMeta im = itemStack.getItemMeta();

        im.getPersistentDataContainer().remove(IS_QUICK_SELECT);
        List<String> lore = im.getLore();
        if(lore.size() == 2){
            im.setLore(new ArrayList<>());
        }else{
            // Remove three items from the end of "lore"
            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);
            im.setLore(lore);
        }

        ItemStack is = itemStack.clone();
        is.setItemMeta(im);
        return is;
    }
    // End Quick Selection Row

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getClick() == ClickType.DOUBLE_CLICK){ // Cancel double clicks
            return true;
        }
        if(e.getRawSlot() == InventoryView.OUTSIDE){ // Disallow dropping items out of inventory
            return true;
        }

        if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
            if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()){
                return true;
            }

            // Increase/Decrease on More Items...
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&bMore Items..."))){
                ItemStack item = this.primary ? this.state.costStack : this.state.costStack2;

                // If this is a custom item, you can't do this.
                if(item == null || !item.equals(this.getInventory().getItem(22))){
                    return true;
                }

                // If the item in the slot is not null and is similar to the stored item, increase/decrease as required
                if(this.getInventory().getItem(22) != null && this.getInventory().getItem(22).isSimilar(item)){
                    if(e.getClick() == ClickType.SHIFT_LEFT){
                        this.getInventory().getItem(22).setAmount(
                                Ints.constrainToRange(this.getInventory().getItem(22).getAmount() + 1, 0,
                                        this.getInventory().getItem(22).getMaxStackSize())
                        );
                    }else if(e.getClick() == ClickType.SHIFT_RIGHT){
                        this.getInventory().getItem(22).setAmount(
                                Ints.constrainToRange(this.getInventory().getItem(22).getAmount() - 1, 0,
                                        this.getInventory().getItem(22).getMaxStackSize())
                        );
                    }
                }

                // Set the new item into the cost field
                if(this.primary){
                    this.state.costStack = this.getInventory().getItem(22);
                }else{
                    this.state.costStack2 = this.getInventory().getItem(22);
                }
            }

            return true;
        }

        if(e.getRawSlot() == 22){

            // Input Slot
            if(primary) {
                if (!this.state.costStack.getType().isAir() && this.state.costStack.equals(e.getCurrentItem())) {
                    this.getInventory().setItem(22, null);
                    this.state.costStack = new ItemStack(Material.AIR);
                    return true;
                }
            }else {
                if (!this.state.costStack2.getType().isAir() && this.state.costStack2.equals(e.getCurrentItem())) {
                    this.getInventory().setItem(22, null);
                    this.state.costStack2 = new ItemStack(Material.AIR);
                    return true;
                }
            }
            return false;

        }else if(e.getView().getInventory(e.getRawSlot()).getType() == InventoryType.PLAYER){

            // Player Inventory Area
            return false;

        }else if(e.getCurrentItem() == null) {
            return true;
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&bMore Items..."))){

            // More Items... Menu
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(primary, state, 1, GUIItemSelector.EnumItemSelectorFilter.ALL, null));
            return true;

        }else if(
                e.getView().getInventory(e.getRawSlot()).getType() != InventoryType.PLAYER &&
                e.getSlot() >= 4 * 9 && e.getSlot() <= (4 * 9) + 8
        ){

            // PRICE QUICK SELECTION
            if(!e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(IS_QUICK_SELECT, PersistentDataType.BYTE)){
                return true;
            }

            ItemStack item = this.primary ? this.state.costStack : this.state.costStack2;

            // If custom item, return it to the user.
            if(this.getInventory().getItem(22) != null && !item.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }

            ItemStack trueItemStack = getTrueItemStack(e.getCurrentItem());
            // If the item is the same as selected, increase/decrease values as needed
            if(this.getInventory().getItem(22) != null && this.getInventory().getItem(22).isSimilar(trueItemStack)){
                if(e.getClick() == ClickType.LEFT){
                    this.getInventory().getItem(22).setAmount(
                            Ints.constrainToRange(this.getInventory().getItem(22).getAmount() + 1, 0,
                                    this.getInventory().getItem(22).getMaxStackSize())
                    );
                }else if(e.getClick() == ClickType.RIGHT){
                    this.getInventory().getItem(22).setAmount(
                            Ints.constrainToRange(this.getInventory().getItem(22).getAmount() - 1, 0,
                                    this.getInventory().getItem(22).getMaxStackSize())
                    );
                }
            }else{
                // Otherwise, return the item.
                this.getInventory().setItem(22, trueItemStack);
            }

            // Set the new item into the cost field
            if(primary){
                this.state.costStack = this.getInventory().getItem(22);
            }else{
                this.state.costStack2 = this.getInventory().getItem(22);
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aOK"))){

            // Okay
            if(primary){
                if(this.getInventory().getItem(22) != null && !this.state.costStack.equals(this.getInventory().getItem(22))){
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
                this.state.costStack = this.getInventory().getItem(22);
                this.state.discount = 0;
            }else{
                if(this.getInventory().getItem(22) != null && !this.state.costStack2.equals(this.getInventory().getItem(22))){
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
                this.state.costStack2 = this.getInventory().getItem(22);
            }
            GUIManager.getInstance().openGUI(this.player, new GUIProduct(state));

        }
        return true;
    }

    @Override
    public void close() {
        if(primary) {
            if (this.getInventory().getItem(22) != null && !this.state.costStack.equals(this.getInventory().getItem(22))) {
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
        }else{
            if(this.getInventory().getItem(22) != null && !this.state.costStack2.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
        }
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
