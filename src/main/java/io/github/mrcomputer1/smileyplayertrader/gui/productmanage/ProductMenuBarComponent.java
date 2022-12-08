package io.github.mrcomputer1.smileyplayertrader.gui.productmanage;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIComponent;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ProductMenuBarComponent extends GUIComponent {

    public enum EnumProductEditPage{
        PRODUCT_SETTINGS,
        PRIORITY,
        DISCOUNT,
        PURCHASE_LIMIT
    }

    private static final ItemStack BACKGROUND = GUI.createItem(Material.IRON_BARS, 1, ChatColor.RESET.toString());
    private static final ItemStack PRODUCT_SETTING = GUI.createItem(Material.EMERALD, 1, I18N.translate("&eProduct Settings"));
    private static final ItemStack PRIORITY_SETTING = GUI.createItemWithLore(
            Material.NETHER_STAR, 1,
            I18N.translate("&eSet Priority"),
            I18N.translate("&eHigher priorities appear higher in the trade list.")
    );
    private static final ItemStack DISCOUNT_SETTING = GUI.createItem(Material.IRON_INGOT, 1, I18N.translate("&eSet Optional Discount"));
    private static final ItemStack PURCHASE_LIMIT = GUI.createItem(Material.LIGHT_BLUE_WOOL, 1, I18N.translate("&ePurchase Limit"));
    private static final ItemStack HIDE_ON_OUT_OF_STOCK = GUI.createItemWithLore(
            Material.YELLOW_WOOL, 1,
            I18N.translate("&eToggle hide on out of stock"),
            I18N.translate("&eWhen enabled, this trade will be hidden when out of stock.")
    );

    private final ProductState state;
    private final EnumProductEditPage editPage;

    private Inventory inventory;
    private ItemStack hideOnOutOfStock;

    public ProductMenuBarComponent(int row, ProductState state, EnumProductEditPage editPage) {
        super(0, row, 9, 1);

        this.state = state;
        this.editPage = editPage;
    }

    private void renderSelected(ItemStack stack){
        ItemMeta im = stack.getItemMeta();
        assert im != null;
        List<String> lore;
        if(im.hasLore()) {
            //noinspection ConstantConditions
            lore = new ArrayList<>(im.getLore());
        }else{
            lore = new ArrayList<>();
        }
        lore.add(0, I18N.translate("&aSelected"));
        im.setLore(lore);
        stack.setItemMeta(im);
    }

    private void updateHideOnOutOfStock(){
        ItemMeta im = this.hideOnOutOfStock.getItemMeta();
        assert im != null;

        if(this.state.hideOnOutOfStock) {
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }else{
            im.removeEnchant(Enchantment.DURABILITY);
            im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        this.hideOnOutOfStock.setItemMeta(im);
    }

    @Override
    public void render(Inventory inventory) {
        this.inventory = inventory;

        // Background
        for(int i = 0; i < this.width; i++)
            inventory.setItem(GUI.toSlot(i, this.y), BACKGROUND.clone());

        // Product Settings
        ItemStack productSetting = PRODUCT_SETTING.clone();
        if(this.editPage == EnumProductEditPage.PRODUCT_SETTINGS)
            renderSelected(productSetting);
        inventory.setItem(GUI.toSlot(0, this.y), productSetting);

        // Priority
        ItemStack prioritySetting = PRIORITY_SETTING.clone();
        if(this.editPage == EnumProductEditPage.PRIORITY)
            renderSelected(prioritySetting);
        inventory.setItem(GUI.toSlot(1, this.y), prioritySetting);

        // Discount
        ItemStack discountSetting = DISCOUNT_SETTING.clone();
        if(this.editPage == EnumProductEditPage.DISCOUNT)
            renderSelected(discountSetting);
        inventory.setItem(GUI.toSlot(2, this.y), discountSetting);

        // Purchase Limit
        ItemStack purchaseLimit = PURCHASE_LIMIT.clone();
        if(this.editPage == EnumProductEditPage.PURCHASE_LIMIT)
            renderSelected(purchaseLimit);
        inventory.setItem(GUI.toSlot(3, this.y), purchaseLimit);

        // Out of Stock Behaviour
        switch(SmileyPlayerTrader.getInstance().getConfiguration().getOutOfStockBehaviour()){
            case HIDE_BY_DEFAULT:
            case SHOW_BY_DEFAULT:
                this.hideOnOutOfStock = HIDE_ON_OUT_OF_STOCK.clone();
                this.updateHideOnOutOfStock();
                inventory.setItem(GUI.toSlot(4, this.y), this.hideOnOutOfStock);
        }
    }

    @Override
    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack) {
        if(x == 0){ // Product Settings
            GUIManager.getInstance().openGui(player, new GUIProduct(this.state));
        }else if(x == 1){ // Priority
            GUIManager.getInstance().openGui(player, new GUIPriority(this.state));
        }else if(x == 2) { // Discount
            GUIManager.getInstance().openGui(player, new GUIDiscount(this.state));
        }else if(x == 3) { // Purchase Limit
            GUIManager.getInstance().openGui(player, new GUIPurchaseLimit(this.state));
        }else if(x == 4 && this.hideOnOutOfStock != null){ // Out of stock behaviour
            this.state.hideOnOutOfStock = !this.state.hideOnOutOfStock;
            this.updateHideOnOutOfStock();
            this.inventory.setItem(GUI.toSlot(4, this.y), this.hideOnOutOfStock);
        }
        return false;
    }
}
