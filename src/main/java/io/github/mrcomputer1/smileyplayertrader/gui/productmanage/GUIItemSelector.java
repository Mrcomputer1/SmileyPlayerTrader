package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ButtonComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.ItemGridComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUIItemSelector extends GUI {

    public enum EnumItemSelectorFilter{
        ALL,
        BLOCKS,
        ITEMS,
        FEATURED
    }

    private final ProductState state;
    private final boolean isPrimary;

    private final int page;
    private final EnumItemSelectorFilter filter;

    private int filterButtonIndex = 1;
    private final List<ItemStack> itemStacks;

    public GUIItemSelector(Player uiPlayer, ProductState state, boolean isPrimary) {
        this(uiPlayer, state, isPrimary, 1, EnumItemSelectorFilter.ALL, null);
    }

    public GUIItemSelector(Player uiPlayer, ProductState state, boolean isPrimary, int page, EnumItemSelectorFilter filter, List<ItemStack> itemStacks){
        super(I18N.translate("&2Select an Item"), 6);

        if(GeyserUtil.isBedrockPlayer(uiPlayer))
            this.setBackgroundFillItem(GUI.BACKGROUND_BEDROCK);

        this.state = state;
        this.isPrimary = isPrimary;

        this.page = page;
        this.filter = filter;

        // Menu Bar
        this.addFilterButton(EnumItemSelectorFilter.ALL, Material.CHEST, "&eAll Items");
        this.addFilterButton(EnumItemSelectorFilter.FEATURED, Material.NETHER_STAR, "&eFeatured");
        this.addFilterButton(EnumItemSelectorFilter.BLOCKS, Material.BRICKS, "&eBlocks");
        this.addFilterButton(EnumItemSelectorFilter.ITEMS, Material.STICK, "&eItems");

        // Item Grid
        this.itemStacks = itemStacks == null ? new ArrayList<>() : itemStacks;
        this.setupItems();

        // Back
        ButtonComponent backBtn = new ButtonComponent(
                0, 5, Material.ARROW, 1, I18N.translate("&aPrevious Page")
        );
        backBtn.setOnClickEvent(this::onBackClick);
        this.addChild(backBtn);

        // Next
        ButtonComponent nextBtn = new ButtonComponent(
                8, 5, Material.ARROW, 1, I18N.translate("&aNext Page")
        );
        nextBtn.setOnClickEvent(this::onNextClick);
        this.addChild(nextBtn);

        // Cancel
        ButtonComponent cancelBtn = new ButtonComponent(
                4, 5, Material.BARRIER, 1, I18N.translate("&cCancel")
        );
        cancelBtn.setOnClickEvent(this::onCancelClick);
        this.addChild(cancelBtn);
    }

    private boolean onCancelClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUISetCost(
                this.getPlayer(), this.state, this.isPrimary
        ));
        return false;
    }

    private boolean onNextClick(ClickType clickType) {
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIItemSelector(
                this.getPlayer(), this.state, this.isPrimary, this.page + 1, this.filter, this.itemStacks
        ));
        return false;
    }

    private boolean onBackClick(ClickType clickType) {
        if(this.page - 1 <= 0)
            return false;
        GUIManager.getInstance().openGui(this.getPlayer(), new GUIItemSelector(
                this.getPlayer(), this.state, this.isPrimary, this.page - 1, this.filter, this.itemStacks
        ));
        return false;
    }

    private void setupItems(){
        List<String> hiddenItems = SmileyPlayerTrader.getInstance().getConfiguration().getPriceSelectorMenuHiddenItems();
        if(this.itemStacks.size() == 0){
            if(this.filter != EnumItemSelectorFilter.FEATURED) {
                if (SmileyPlayerTrader.getInstance().getConfiguration().getPriceSelectorMenuAutomaticAddVanilla()) {
                    // Vanilla Items
                    for (Material m : Material.values()) {
                        if (ItemUtil.isHiddenItem(hiddenItems, m)) {
                            continue;
                        }

                        if ((m.isItem() && !m.isAir()) && this.filter == EnumItemSelectorFilter.ALL) {
                            this.itemStacks.add(prepareItemStack(new ItemStack(m)));
                        } else if ((m.isItem() && !m.isAir() && !m.isBlock()) && this.filter == EnumItemSelectorFilter.ITEMS) {
                            this.itemStacks.add(prepareItemStack(new ItemStack(m)));
                        } else if ((m.isItem() && !m.isAir() && m.isBlock()) && this.filter == EnumItemSelectorFilter.BLOCKS) {
                            this.itemStacks.add(prepareItemStack(new ItemStack(m)));
                        }
                    }
                }

                // Extra Items
                List<ItemStack> extraItems = SmileyPlayerTrader.getInstance().getConfiguration().getPriceSelectorMenuExtraItems();
                for (ItemStack item : extraItems) {
                    ItemStack is = prepareItemStack(item);

                    if (is == null)
                        continue;

                    if (this.filter == EnumItemSelectorFilter.ALL) {
                        this.itemStacks.add(is);
                    } else if (this.filter == EnumItemSelectorFilter.ITEMS && !is.getType().isBlock()) {
                        this.itemStacks.add(is);
                    } else if (this.filter == EnumItemSelectorFilter.BLOCKS && is.getType().isBlock()) {
                        this.itemStacks.add(is);
                    }
                }
            }else{
                // Featured Items
                List<ItemStack> featuredItems = SmileyPlayerTrader.getInstance().getConfiguration().getPriceSelectorMenuFeaturedItems();
                for(ItemStack item : featuredItems){
                    ItemStack is = prepareItemStack(item);

                    if(is == null)
                        continue;

                    this.itemStacks.add(is);
                }
            }
        }

        ItemStack[] pagedStacks = new ItemStack[28];
        int index = 0;
        for(int i = (this.page - 1) * 28; i < this.page * 28; i++){
            if(i >= this.itemStacks.size())
                break;
            pagedStacks[index++] = this.itemStacks.get(i);
        }

        ItemGridComponent itemGrid = new ItemGridComponent(
                1, 1, 7, 4
        );
        itemGrid.getItems().addAll(Arrays.asList(pagedStacks));
        itemGrid.setOnClickEvent(this::onItemClick);
        this.addChild(itemGrid);
    }

    private boolean onItemClick(ClickType clickType, ItemStack itemStack) {
        if(this.isPrimary){
            this.state.costStack = itemStack.clone();
        }else{
            this.state.costStack2 = itemStack.clone();
        }
        GUIManager.getInstance().openGui(this.getPlayer(), new GUISetCost(
                this.getPlayer(), this.state, this.isPrimary
        ));
        return false;
    }

    private ItemStack prepareItemStack(ItemStack is){
        if(is == null)
            return null;
        ItemMeta im = is.getItemMeta();
        assert im != null;
        is.setItemMeta(im);
        return is;
    }

    private void addFilterButton(EnumItemSelectorFilter filter, Material material, String name){
        ButtonComponent btn = new ButtonComponent(
                filterButtonIndex++, 0, material, 1, I18N.translate(name)
        );
        if(btn.getX() > 7){
            SmileyPlayerTrader.getInstance().getLogger().warning("Too many item selector filter buttons.");
            return;
        }

        btn.setOnClickEvent((clickType) -> {
            GUIManager.getInstance().openGui(this.getPlayer(), new GUIItemSelector(
                    this.getPlayer(), this.state, this.isPrimary, 1, filter, null
            ));
            return false;
        });
        this.addChild(btn);
    }

}
