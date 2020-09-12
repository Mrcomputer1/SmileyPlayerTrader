package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class GUISetCost extends AbstractGUI {
    private Player player;
    private boolean primary;
    private boolean editing;
    private long productId;
    private ItemStack product;
    private ItemStack cost1;
    private ItemStack cost2;

    private int page;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack INSERT_LBL = AbstractGUI.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&eInsert price"));
    private static ItemStack IRON_BTN = AbstractGUI.createItemWithLore(Material.IRON_INGOT, 1, I18N.translate("&7Iron"), I18N.translate("&eClick to increase value"), I18N.translate("&eRight Click to decrease value"));
    private static ItemStack GOLD_BTN = AbstractGUI.createItemWithLore(Material.GOLD_INGOT, 1, I18N.translate("&6Gold"), I18N.translate("&eClick to increase value"), I18N.translate("&eRight Click to decrease value"));
    private static ItemStack EMERALD_BTN = AbstractGUI.createItemWithLore(Material.EMERALD, 1, I18N.translate("&aEmerald"), I18N.translate("&eClick to increase value"), I18N.translate("&eRight Click to decrease value"));
    private static ItemStack DIAMOND_BTN = AbstractGUI.createItemWithLore(Material.DIAMOND, 1, I18N.translate("&bDiamond"), I18N.translate("&eClick to increase value"), I18N.translate("&eRight Click to decrease value"));
    private static ItemStack OK_BTN = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aOK"));

    public GUISetCost(int page, boolean primary, boolean editing, long productId, ItemStack product, ItemStack cost1, ItemStack cost2){
        this.primary = primary;
        this.editing = editing;
        this.productId = productId;
        this.product = product;
        this.cost1 = cost1;
        this.cost2 = cost2;
        this.page = page;

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

        this.getInventory().setItem((4 * 9), BORDER.clone());
        this.getInventory().setItem((4 * 9) + 1, IRON_BTN.clone());
        this.getInventory().setItem((4 * 9) + 2, BORDER.clone());
        this.getInventory().setItem((4 * 9) + 3, GOLD_BTN.clone());
        this.getInventory().setItem((4 * 9) + 4, BORDER.clone());
        this.getInventory().setItem((4 * 9) + 5, EMERALD_BTN.clone());
        this.getInventory().setItem((4 * 9) + 6, BORDER.clone());
        this.getInventory().setItem((4 * 9) + 7, DIAMOND_BTN.clone());
        this.getInventory().setItem((4 * 9) + 8, BORDER.clone());

        this.getInventory().setItem(5 * 9, OK_BTN.clone());
        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 1, 8, BORDER);
    }

    @Override
    public boolean click(InventoryClickEvent e) {
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
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&7Iron"))){

            // IRON
            if(primary) {
                if (this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }else{
                if (this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }
            if (this.getInventory().getItem(22) != null && this.getInventory().getItem(22).getType() == Material.IRON_INGOT) {
                if(e.getClick() == ClickType.LEFT) {
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() + 1);
                }else if(e.getClick() == ClickType.RIGHT){
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() - 1);
                }
            }else{
                this.getInventory().setItem(22, new ItemStack(Material.IRON_INGOT));
            }
            if(primary){
                this.cost1 = this.getInventory().getItem(22);
            }else{
                this.cost2 = this.getInventory().getItem(22);
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&6Gold"))){

            // GOLD
            if(primary) {
                if (this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }else{
                if (this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }
            if (this.getInventory().getItem(22) != null && this.getInventory().getItem(22).getType() == Material.GOLD_INGOT) {
                if(e.getClick() == ClickType.LEFT) {
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() + 1);
                }else if(e.getClick() == ClickType.RIGHT){
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() - 1);
                }
            }else{
                this.getInventory().setItem(22, new ItemStack(Material.GOLD_INGOT));
            }
            if(primary){
                this.cost1 = this.getInventory().getItem(22);
            }else{
                this.cost2 = this.getInventory().getItem(22);
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aEmerald"))){

            // EMERALD
            if(primary) {
                if (this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }else{
                if (this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }
            if (this.getInventory().getItem(22) != null && this.getInventory().getItem(22).getType() == Material.EMERALD) {
                if(e.getClick() == ClickType.LEFT) {
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() + 1);
                }else if(e.getClick() == ClickType.RIGHT){
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() - 1);
                }
            }else{
                this.getInventory().setItem(22, new ItemStack(Material.EMERALD));
            }
            if(primary){
                this.cost1 = this.getInventory().getItem(22);
            }else{
                this.cost2 = this.getInventory().getItem(22);
            }

        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&bDiamond"))){

            // DIAMOND
            if(primary) {
                if (this.getInventory().getItem(22) != null && !this.cost1.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }else{
                if (this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))) {
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
            }
            if (this.getInventory().getItem(22) != null && this.getInventory().getItem(22).getType() == Material.DIAMOND) {
                if(e.getClick() == ClickType.LEFT) {
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() + 1);
                }else if(e.getClick() == ClickType.RIGHT){
                    this.getInventory().getItem(22).setAmount(this.getInventory().getItem(22).getAmount() - 1);
                }
            }else{
                this.getInventory().setItem(22, new ItemStack(Material.DIAMOND));
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
            }else{
                if(this.getInventory().getItem(22) != null && !this.cost2.equals(this.getInventory().getItem(22))){
                    this.player.getInventory().addItem(this.getInventory().getItem(22).clone());
                }
                this.cost2 = this.getInventory().getItem(22);
            }
            GUIManager.getInstance().openGUI(this.player, new GUIProduct(this.page, editing, product, productId, cost1, cost2));

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
