package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import com.google.common.primitives.Ints;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.component.SlotComponent;
import io.github.mrcomputer1.smileyplayertrader.util.GeyserUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriceQuickSelectionComponent extends GUIComponent {

    private static final ItemStack MORE_ITEMS_BTN = GUI.createItemWithLoreAndModify(
            Material.BEACON, 1, I18N.translate("&bMore Items..."),
            null, (meta) -> {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            },
            I18N.translate("&eShift Click to increase value"),
            I18N.translate("&eShift Right Click to decrease value")
    );

    private static final ItemStack MORE_ITEMS_BEDROCK_BTN = GUI.createItemWithLoreAndModify(
            Material.BEACON, 1, I18N.translate("&bMore Items..."),
            null, (meta) -> {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
    );

    private final List<ItemStack> itemStacks = new ArrayList<>();
    private final SlotComponent priceSlot;
    private final ProductState state;
    private final boolean isPrimary;

    private final Player uiPlayer;

    public PriceQuickSelectionComponent(Player uiPlayer, int x, int y, SlotComponent priceSlot, ProductState state, boolean isPrimary) {
        super(x, y, 7, 1);

        this.uiPlayer = uiPlayer;

        this.priceSlot = priceSlot;

        List<ItemStack> quickSelectionStacks = SmileyPlayerTrader.getInstance().getConfiguration().getPriceQuickSelection();
        int stackCount = 0;
        for(ItemStack item : quickSelectionStacks){
            if(stackCount++ >= 6){
                SmileyPlayerTrader.getInstance().getLogger().warning("You have too many quick selection items.");
                break;
            }

            ItemStack stack = createQuickSelectionItem(item);
            if(stack != null)
                this.itemStacks.add(stack);
        }

        if(GeyserUtil.isBedrockPlayer(this.uiPlayer)){
            this.itemStacks.add(MORE_ITEMS_BEDROCK_BTN);
        }else this.itemStacks.add(MORE_ITEMS_BTN);

        this.state = state;
        this.isPrimary = isPrimary;
    }

    private ItemStack createQuickSelectionItem(ItemStack is){
        if(is == null)
            return null;

        if(!GeyserUtil.isBedrockPlayer(this.uiPlayer)) {
            ItemMeta im = is.getItemMeta();
            assert im != null;

            if (im.hasLore()) {
                List<String> lore = im.getLore();
                assert lore != null;
                lore.add("");
                lore.add(I18N.translate("&eClick to increase value"));
                lore.add(I18N.translate("&eRight Click to decrease value"));
                im.setLore(lore);
            } else {
                im.setLore(Arrays.asList(
                        I18N.translate("&eClick to increase value"),
                        I18N.translate("&eRight Click to decrease value")
                ));
            }

            is.setItemMeta(im);
        }

        return is;
    }

    private ItemStack getTrueItemStack(ItemStack itemStack){
        ItemMeta im = itemStack.getItemMeta();
        assert im != null;

        if(!GeyserUtil.isBedrockPlayer(this.uiPlayer)) {
            List<String> lore = im.getLore();
            if (lore.size() == 2) {
                im.setLore(new ArrayList<>());
            } else {
                // Remove three items from the end of "lore"
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
                im.setLore(lore);
            }
        }

        ItemStack is = itemStack.clone();
        is.setItemMeta(im);

        return is;
    }

    @Override
    public void render(Inventory inventory) {
        // probably a better way to write this, but this works.
        switch(itemStacks.size()){
            case 1:
                inventory.setItem((this.y * 9) + 4, itemStacks.get(0));
                break;
            case 2:
                inventory.setItem((this.y * 9) + 3, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 5, itemStacks.get(1));
                break;
            case 3:
                inventory.setItem((this.y * 9) + 2, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 4, itemStacks.get(1));
                inventory.setItem((this.y * 9) + 6, itemStacks.get(2));
                break;
            case 4:
                inventory.setItem((this.y * 9) + 1, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 3, itemStacks.get(1));
                inventory.setItem((this.y * 9) + 5, itemStacks.get(2));
                inventory.setItem((this.y * 9) + 7, itemStacks.get(3));
                break;
            case 5:
                inventory.setItem((this.y * 9) + 2, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 3, itemStacks.get(1));
                inventory.setItem((this.y * 9) + 4, itemStacks.get(2));
                inventory.setItem((this.y * 9) + 5, itemStacks.get(3));
                inventory.setItem((this.y * 9) + 6, itemStacks.get(4));
                break;
            case 6:
                inventory.setItem((this.y * 9) + 1, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 2, itemStacks.get(1));
                inventory.setItem((this.y * 9) + 3, itemStacks.get(2));
                inventory.setItem((this.y * 9) + 4, itemStacks.get(3));
                inventory.setItem((this.y * 9) + 5, itemStacks.get(4));
                inventory.setItem((this.y * 9) + 7, itemStacks.get(5));
                break;
            case 7:
                inventory.setItem((this.y * 9) + 1, itemStacks.get(0));
                inventory.setItem((this.y * 9) + 2, itemStacks.get(1));
                inventory.setItem((this.y * 9) + 3, itemStacks.get(2));
                inventory.setItem((this.y * 9) + 4, itemStacks.get(3));
                inventory.setItem((this.y * 9) + 5, itemStacks.get(4));
                inventory.setItem((this.y * 9) + 6, itemStacks.get(5));
                inventory.setItem((this.y * 9) + 7, itemStacks.get(6));
                break;
            default:
                throw new IllegalArgumentException("Unsupported amount of items");
        }

        ItemStack background = GeyserUtil.isBedrockPlayer(this.uiPlayer) ? GUI.BACKGROUND_BEDROCK : GUI.BACKGROUND;
        for(int i = this.y * 9; i < (this.y + 1) * 9; i++){
            if(inventory.getItem(i) == null){
                inventory.setItem(i, background.clone());
            }
        }
    }

    @Override
    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack) {
        if(clickedStack.equals(GUI.BACKGROUND) || clickedStack.equals(GUI.BACKGROUND_BEDROCK))
            return false;

        // More Items...
        if(clickedStack.equals(MORE_ITEMS_BTN) || clickedStack.equals(MORE_ITEMS_BEDROCK_BTN)){
            if(type == ClickType.LEFT){
                GUIManager.getInstance().openGui(player, new GUIItemSelector(
                        this.uiPlayer, this.state, this.isPrimary
                ));
            }else if(type == ClickType.SHIFT_LEFT){
                // If this is a custom item, you can't do this.
                if(this.priceSlot.isChanged())
                    return false;

                // If item slot is empty, ignore.
                if(this.priceSlot.getItem() == null || this.priceSlot.getItem().getType().isAir())
                    return false;

                // Increase amount
                this.priceSlot.getItem().setAmount(
                        Ints.constrainToRange(this.priceSlot.getItem().getAmount() + 1, 0, this.priceSlot.getItem().getMaxStackSize())
                );
                this.priceSlot.updateItem(player, false);
            }else if(type == ClickType.SHIFT_RIGHT){
                // If this is a custom item, you can't do this.
                if(this.priceSlot.isChanged())
                    return false;

                // If item slot is empty, ignore.
                if(this.priceSlot.getItem() == null || this.priceSlot.getItem().getType().isAir())
                    return false;

                // Increase amount
                this.priceSlot.getItem().setAmount(
                        Ints.constrainToRange(this.priceSlot.getItem().getAmount() - 1, 0, this.priceSlot.getItem().getMaxStackSize())
                );
                this.priceSlot.updateItem(player, false);
            }
        }else{
            if(type == ClickType.LEFT){
                ItemStack trueItemStack = getTrueItemStack(clickedStack);

                // If item in slot is not similar, set it.
                if(this.priceSlot.getItem() == null || !this.priceSlot.getItem().isSimilar(trueItemStack) || this.priceSlot.isChanged()) {
                    if(this.priceSlot.isChanged())
                        this.priceSlot.updateItem(player, true);
                    this.priceSlot.setItem(trueItemStack);
                    this.priceSlot.updateItem(player, false);
                }else{
                    this.priceSlot.getItem().setAmount(
                            Ints.constrainToRange(this.priceSlot.getItem().getAmount() + 1, 0, this.priceSlot.getItem().getMaxStackSize())
                    );
                }
            }else if(type == ClickType.RIGHT){
                ItemStack trueItemStack = getTrueItemStack(clickedStack);

                // If item in slot is not similar, set it.
                if(this.priceSlot.getItem() == null || !this.priceSlot.getItem().isSimilar(trueItemStack) || this.priceSlot.isChanged()) {
                    if(this.priceSlot.isChanged())
                        this.priceSlot.updateItem(player, true);
                    this.priceSlot.setItem(trueItemStack);
                    this.priceSlot.updateItem(player, false);
                }else{
                    this.priceSlot.getItem().setAmount(
                            Ints.constrainToRange(this.priceSlot.getItem().getAmount() - 1, 0, this.priceSlot.getItem().getMaxStackSize())
                    );
                }
            }
        }

        return false;
    }

}
