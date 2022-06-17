package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class GUIItemStorage extends AbstractGUI{

    private Player player;
    private int page;
    private long id;
    private int storedProduct;
    private ItemStack product;

    private static ItemStack BORDER = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INFO_TEMPLATE = createItemWithLore(
            Material.YELLOW_STAINED_GLASS_PANE, 1, "&eStored Product: %0%",
            I18N.translate("&eClick on the product in your inventory to store it.")
    );
    private static ItemStack WITHDRAW_1_BTN = createItem(Material.CHEST, 1, I18N.translate("&eWithdraw 1"));
    private static ItemStack WITHDRAW_8_BTN = createItem(Material.CHEST, 8, I18N.translate("&eWithdraw 8"));
    private static ItemStack WITHDRAW_ALL_BTN = createItem(Material.CHEST, 64, I18N.translate("&eWithdraw ALL"));
    private static ItemStack OK_BTN = createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aOK"));

    public GUIItemStorage(int page, long id, int storedProduct, ItemStack product){
        this.page = page;
        this.id = id;
        this.storedProduct = storedProduct;
        this.product  = product;

        this.createInventory(I18N.translate("&2Manage Stored Items"), 6);

        GUIUtil.drawLine(this.getInventory(), 0 * 9, 9, BORDER);

        GUIUtil.drawLine(this.getInventory(), 1 * 9, 4, BORDER);
        drawInfoPane();
        GUIUtil.drawLine(this.getInventory(), (1 * 9) + 5, 4, BORDER);

        GUIUtil.drawLine(this.getInventory(), 2 * 9, 9, BORDER);

        GUIUtil.drawLine(this.getInventory(), 3 * 9, 9, BORDER);
        this.getInventory().setItem((3 * 9) + 2, WITHDRAW_1_BTN.clone());
        this.getInventory().setItem((3 * 9) + 4, WITHDRAW_8_BTN.clone());
        this.getInventory().setItem((3 * 9) + 6, WITHDRAW_ALL_BTN.clone());

        GUIUtil.drawLine(this.getInventory(), 4 * 9, 9, BORDER);

        this.getInventory().setItem(5 * 9, OK_BTN.clone());
        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 1, 8, BORDER);
    }

    private void drawInfoPane(){
        ItemStack is = INFO_TEMPLATE.clone();
        ItemMeta im = is.getItemMeta();
        //noinspection ConstantConditions
        im.setDisplayName(I18N.translate(is.getItemMeta().getDisplayName(), this.storedProduct));
        is.setItemMeta(im);
        this.getInventory().setItem((1 * 9) + 4, is);
    }

    private void withdraw(int limit){
        ItemStack stack = this.product.clone();

        int countAvailable = this.storedProduct;

        if(limit > countAvailable){
            limit = countAvailable;
        }

        if(limit <= 0){
            player.sendMessage(I18N.translate("&cYou do not have enough of that product."));
            return;
        }

        int itemAmount = limit * stack.getAmount();
        stack.setAmount(itemAmount);

        Map<Integer, ItemStack> errs = player.getInventory().addItem(stack);
        for(ItemStack is : errs.values()){
            player.getWorld().dropItem(player.getLocation(), is);
        }

        this.storedProduct -= limit;
        this.drawInfoPane();

        SmileyPlayerTrader.getInstance().getStatementHandler().run(
                StatementHandler.StatementType.CHANGE_STORED_PRODUCT,
                -limit, this.id
        );
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getRawSlot() > 53){

            // Deposit
            ItemStack clicked = e.getCurrentItem();
            if(clicked == null)
                return true;

            ItemStack product = this.product;

            if(clicked.isSimilar(product)) {
                int amountOfProduct = clicked.getAmount() / product.getAmount();
                int itemAmount = amountOfProduct * product.getAmount();

                int limit = SmileyPlayerTrader.getInstance().getConfig().getInt("itemStorage.productStorageLimit", -1);
                if (limit != -1 && this.storedProduct + amountOfProduct > limit) {
                    player.sendMessage(I18N.translate("&cYou cannot store more than %0% of a product.", limit));
                    return true;
                }

                // Add to storage
                this.storedProduct += amountOfProduct;
                SmileyPlayerTrader.getInstance().getStatementHandler().run(
                        StatementHandler.StatementType.CHANGE_STORED_PRODUCT,
                        amountOfProduct, this.id
                );
                // Remove from hand
                clicked.setAmount(clicked.getAmount() - itemAmount);

                this.drawInfoPane();
            }

        }else{

            // Withdraw Buttons
            //noinspection ConstantConditions
            if(e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){

                if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eWithdraw 1"))){

                    // Withdraw 1
                    this.withdraw(1);

                }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eWithdraw 8"))){

                    // Withdraw 8
                    this.withdraw(8);

                }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&eWithdraw ALL"))){

                    // Withdraw All
                    this.withdraw(Integer.MAX_VALUE);

                }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aOK"))){

                    // OK
                    GUIManager.getInstance().openGUI(this.player, new GUIListItems(this.page));

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
    }

}
