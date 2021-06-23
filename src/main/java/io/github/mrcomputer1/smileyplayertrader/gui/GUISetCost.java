package io.github.mrcomputer1.smileyplayertrader.gui;

import com.google.common.primitives.Ints;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
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
    private boolean editing;
    private long productId;
    private ItemStack product;
    private ItemStack cost1;
    private ItemStack cost2;
    private int discount;

    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INSERT_LBL = AbstractGUI.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&eInsert Price"));
    private static ItemStack OK_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aOK"));
    private static ItemStack MORE_ITEMS_BTN = AbstractGUI.createItem(Material.BEACON, 1, I18N.translate("&bMore Items..."));

    static{
        ItemMeta im = MORE_ITEMS_BTN.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        MORE_ITEMS_BTN.setItemMeta(im);
    }

    private static NamespacedKey IS_QUICK_SELECT = new NamespacedKey(SmileyPlayerTrader.getInstance(), "is_quick_select");

    public GUISetCost(int page, boolean primary, boolean editing, long productId, ItemStack product, ItemStack cost1, ItemStack cost2, int discount){
        this.primary = primary;
        this.editing = editing;
        this.productId = productId;
        this.product = product;
        this.cost1 = cost1;
        this.cost2 = cost2;
        this.page = page;
        this.discount = discount;

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
            this.getInventory().setItem((2 * 9) + 4, this.cost1.clone());
        }else{
            this.getInventory().setItem((2 * 9) + 4, this.cost2.clone());
        }
        GUIUtil.drawLine(this.getInventory(), (2 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 3, BORDER);

        // Quick Selection Row
        List<LinkedHashMap<String, Object>> priceQuickSelection = (List<LinkedHashMap<String, Object>>) SmileyPlayerTrader.getInstance().getConfig().getList("priceQuickSelection", new ArrayList<>());
        List<ItemStack> stacks = new ArrayList<>();
        for(LinkedHashMap<String, Object> item : priceQuickSelection){
            stacks.add(createQuickSelectionRowItem(item));
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
        if(e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){ // Cancel double clicks and shift clicks
            return true;
        }
        if(e.getRawSlot() == InventoryView.OUTSIDE){ // Disallow dropping items out of inventory
            return true;
        }

        if(e.getRawSlot() == 22){

            // Input Slot
            if(primary) {
                if (!this.cost1.getType().isAir() && this.cost1.equals(e.getCurrentItem())) {
                    this.getInventory().setItem(22, null);
                    this.cost1 = new ItemStack(Material.AIR);
                    return true;
                }
            }else {
                if (!this.cost2.getType().isAir() && this.cost2.equals(e.getCurrentItem())) {
                    this.getInventory().setItem(22, null);
                    this.cost2 = new ItemStack(Material.AIR);
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
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(this.page, primary, editing, productId, product, cost1, cost2, discount, 1, GUIItemSelector.EnumItemSelectorFilter.ALL, null));
            return true;
        }else if(
                e.getView().getInventory(e.getRawSlot()).getType() != InventoryType.PLAYER &&
                e.getSlot() >= 4 * 9 && e.getSlot() <= (4 * 9) + 8
        ){

            // PRICE QUICK SELECTION
            if(!e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(IS_QUICK_SELECT, PersistentDataType.BYTE)){
                return true;
            }

            ItemStack item = this.primary ? this.cost1 : this.cost2;

            if(this.getInventory().getItem(22) != null && !item.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }

            ItemStack trueItemStack = getTrueItemStack(e.getCurrentItem());
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
                this.getInventory().setItem(22, trueItemStack);
            }

            if(primary){
                this.cost1 = this.getInventory().getItem(22);
            }else{
                this.cost2 = this.getInventory().getItem(22);
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aOK"))){

            // Okay
            if(primary){
                if(this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))){
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
                this.cost1 = this.getInventory().getItem(22);
                this.discount = 0;
            }else{
                if(this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))){
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
                this.cost2 = this.getInventory().getItem(22);
            }
            GUIManager.getInstance().openGUI(this.player, new GUIProduct(this.page, editing, product, productId, cost1, cost2, discount));

        }
        return true;
    }

    @Override
    public void close() {
        if(primary) {
            if (this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))) {
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
        }else{
            if(this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))){
                this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
            }
        }
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
