package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GUIItemSelector extends AbstractGUI {

    public enum EnumItemSelectorFilter{
        ALL,
        BLOCKS,
        ITEMS,
        FEATURED,

        // Integrations
    }

    private Player player;
    private boolean primary;
    private boolean editing;
    private long productId;
    private ItemStack product;
    private ItemStack cost1;
    private ItemStack cost2;
    private int discount;

    private int page;

    private int itemSelectorPage;
    private EnumItemSelectorFilter selectorFilter;
    private List<ItemStack> itemStacks;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack ALL_BTN = AbstractGUI.createItem(Material.CHEST, 1, I18N.translate("&eAll Items"));
    private static ItemStack FEATURED_BTN = AbstractGUI.createItem(Material.NETHER_STAR, 1, I18N.translate("&eFeatured"));
    private static ItemStack BLOCKS_BTN = AbstractGUI.createItem(Material.BRICKS, 1, I18N.translate("&eBlocks"));
    private static ItemStack ITEMS_BTN = AbstractGUI.createItem(Material.STICK, 1, I18N.translate("&eItems"));
    private static ItemStack PREV_BTN = AbstractGUI.createItem(Material.ARROW, 1, I18N.translate("&aPrevious Page"));
    private static ItemStack NEXT_BTN = AbstractGUI.createItem(Material.ARROW, 1, I18N.translate("&aNext Page"));
    private static ItemStack CANCEL_BTN = AbstractGUI.createItem(Material.BARRIER, 1, I18N.translate("&cCancel"));

    // Integrations

    private static NamespacedKey IS_ITEM = new NamespacedKey(SmileyPlayerTrader.getInstance(), "is_item");

    public GUIItemSelector(int page, boolean primary, boolean editing, long productId, ItemStack product, ItemStack cost1, ItemStack cost2, int discount, int itemSelectorPage, EnumItemSelectorFilter filter, List<ItemStack> itemStacks){
        this.page = page;
        this.primary = primary;
        this.editing = editing;
        this.productId = productId;
        this.product = product;
        this.cost1 = cost1;
        this.cost2 = cost2;
        this.discount = discount;

        this.itemSelectorPage = itemSelectorPage;
        this.selectorFilter = filter;
        this.itemStacks = itemStacks;

        this.createInventory(I18N.translate("&2Select an Item"), 6);

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 1, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 2, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 3, BORDER);
        GUIUtil.fillStartAndEnd(this.getInventory(), 4, BORDER);
        GUIUtil.fillRow(this.getInventory(), 5, BORDER);

        this.getInventory().setItem(1, ALL_BTN.clone());
        this.getInventory().setItem(2, FEATURED_BTN.clone());
        this.getInventory().setItem(3, BLOCKS_BTN.clone());
        this.getInventory().setItem(4, ITEMS_BTN.clone());

        // Integrations
        int integrationPos = 1;

        // End Integrations

        this.getInventory().setItem((5 * 9), PREV_BTN.clone());
        this.getInventory().setItem((5 * 9) + 8, NEXT_BTN.clone());
        this.getInventory().setItem((5 * 9) + 4, CANCEL_BTN.clone());

        List<String> hiddenItems = (List<String>) SmileyPlayerTrader.getInstance().getConfig().getList("priceSelectorMenu.hiddenItems", new ArrayList<>());
        if(itemStacks == null){
            itemStacks = new ArrayList<>();

            if(filter != EnumItemSelectorFilter.FEATURED) {
                if (SmileyPlayerTrader.getInstance().getConfig().getBoolean("priceSelectorMenu.automaticAdd.vanilla", true)) {
                    // Vanilla Items
                    for (Material m : Material.values()) {
                        if (ItemUtil.isHiddenItem(hiddenItems, m)) {
                            continue;
                        }

                        if ((m.isItem() && !m.isAir()) && filter == EnumItemSelectorFilter.ALL) {
                            itemStacks.add(prepareItemStack(new ItemStack(m)));
                        } else if ((m.isItem() && !m.isAir() && !m.isBlock()) && filter == EnumItemSelectorFilter.ITEMS) {
                            itemStacks.add(prepareItemStack(new ItemStack(m)));
                        } else if ((m.isItem() && !m.isAir() && m.isBlock()) && filter == EnumItemSelectorFilter.BLOCKS) {
                            itemStacks.add(prepareItemStack(new ItemStack(m)));
                        }
                    }
                }

                // Extra Items
                List<LinkedHashMap<String, Object>> extraItems = (List<LinkedHashMap<String, Object>>) SmileyPlayerTrader.getInstance().getConfig().getList("priceSelectorMenu.extraItems", new ArrayList<>());
                for (LinkedHashMap<String, Object> item : extraItems) {
                    ItemStack is = prepareItemStack(ItemUtil.buildConfigurationItem(item));

                    if (is == null)
                        continue;

                    if (filter == EnumItemSelectorFilter.ALL) {
                        itemStacks.add(is);
                    } else if (filter == EnumItemSelectorFilter.ITEMS && !is.getType().isBlock()) {
                        itemStacks.add(is);
                    } else if (filter == EnumItemSelectorFilter.BLOCKS && is.getType().isBlock()) {
                        itemStacks.add(is);
                    }
                }

                // Integration All Filter
                if(filter == EnumItemSelectorFilter.ALL){

                }

                // Integration Filters

                // End Integration Filters
            }else{
                // Featured Items
                List<LinkedHashMap<String, Object>> featuredItems = (List<LinkedHashMap<String, Object>>) SmileyPlayerTrader.getInstance().getConfig().getList("priceSelectorMenu.featuredItems", new ArrayList<>());
                for(LinkedHashMap<String, Object> item : featuredItems){
                    ItemStack is = prepareItemStack(ItemUtil.buildConfigurationItem(item));

                    if(is == null)
                        continue;

                    itemStacks.add(is);
                }
            }
        }

        ItemStack[] pagedStacks = new ItemStack[28];
        int index = 0;
        for(int i = (itemSelectorPage - 1) * 28; i < itemSelectorPage * 28; i++){
            if(i >= itemStacks.size())
                break;
            pagedStacks[index++] = itemStacks.get(i);
        }

        GUIUtil.spreadItemsCloned(this.getInventory(), 1, 7, 1, 4, pagedStacks);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getCurrentItem() == null)
            return true;
        if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(IS_ITEM, PersistentDataType.BYTE)){
            ItemStack is = e.getCurrentItem().clone();
            ItemMeta im = is.getItemMeta();
            im.getPersistentDataContainer().remove(IS_ITEM);
            is.setItemMeta(im);
            if(primary){
                cost1 = is;
            }else{
                cost2 = is;
            }
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(page, primary, editing, productId, product, cost1, cost2, discount));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aPrevious Page"))){
            if(itemSelectorPage - 1 <= 0)
                return true;
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, itemSelectorPage - 1, selectorFilter, itemStacks));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aNext Page"))){
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, itemSelectorPage + 1, selectorFilter, itemStacks));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eAll Items"))){
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, 1, EnumItemSelectorFilter.ALL, null));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eFeatured"))){
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, 1, EnumItemSelectorFilter.FEATURED, null));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eBlocks"))){
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, 1, EnumItemSelectorFilter.BLOCKS, null));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eItems"))){
            GUIManager.getInstance().openGUI(this.player, new GUIItemSelector(page, primary, editing, productId, product, cost1, cost2, discount, 1, EnumItemSelectorFilter.ITEMS, null));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cCancel"))){
            GUIManager.getInstance().openGUI(this.player, new GUISetCost(page, primary, editing, productId, product, cost1, cost2, discount));
        }

        return true;
    }

    private ItemStack prepareItemStack(ItemStack is){
        if(is == null)
            return null;
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(IS_ITEM, PersistentDataType.BYTE, (byte) 1);
        is.setItemMeta(im);
        return is;
    }

    @Override
    public void close() {
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
