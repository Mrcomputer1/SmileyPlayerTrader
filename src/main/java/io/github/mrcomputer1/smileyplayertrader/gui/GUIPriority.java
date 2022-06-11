package io.github.mrcomputer1.smileyplayertrader.gui;

import io.github.mrcomputer1.smileyplayertrader.util.GUIUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIPriority extends AbstractGUI{

    private Player player;
    private final ProductGUIState state;

    private static ItemStack BORDER = AbstractGUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    private static ItemStack UP = AbstractGUI.createItem(Material.PLAYER_HEAD, 1, I18N.translate("&aIncrease Priority"));
    private static ItemStack DOWN = AbstractGUI.createItem(Material.PLAYER_HEAD, 1, I18N.translate("&cDecrease Priority"));
    private static ItemStack OK = AbstractGUI.createItem(Material.EMERALD_BLOCK, 1, I18N.translate("&aOK"));
    private static ItemStack RESET = AbstractGUI.createItem(Material.REDSTONE_BLOCK, 1, I18N.translate("&cReset Priority"));

    static{
        SkullMeta sm = (SkullMeta) UP.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowUp"));
        UP.setItemMeta(sm);

        sm = (SkullMeta) DOWN.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowDown"));
        DOWN.setItemMeta(sm);
    }

    public GUIPriority(ProductGUIState state){
        this.state = state;

        this.createInventory(I18N.translate("&2Set Priority"), 6);

        GUIUtil.fillRow(this.getInventory(), 0, BORDER);

        ItemStack discountIS = createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, I18N.translate("&bPriority: %0%", this.state.priority));
        List<String> lore = new ArrayList<>();
        lore.add(I18N.translate("&eHigher priorities appear higher in the trade list."));
        ItemMeta im = discountIS.getItemMeta();
        im.setLore(lore);
        discountIS.setItemMeta(im);

        GUIUtil.drawLine(this.getInventory(), (1 * 9), 4, BORDER);
        this.getInventory().setItem((1 * 9) + 4,
                discountIS);
        GUIUtil.drawLine(this.getInventory(), (1 * 9) + 5, 4, BORDER);

        GUIUtil.fillRow(this.getInventory(), 2, BORDER);

        lore = new ArrayList<>();
        lore.add(I18N.translate("&bPriority: %0%", this.state.priority));
        lore.add(I18N.translate("&bLeft click for &l1"));
        lore.add(I18N.translate("&bRight click for &l10"));

        ItemStack up = UP.clone();
        im = up.getItemMeta();
        im.setLore(lore);
        up.setItemMeta(im);

        ItemStack down = DOWN.clone();
        im = down.getItemMeta();
        im.setLore(lore);
        down.setItemMeta(im);

        GUIUtil.drawLine(this.getInventory(), (3 * 9), 3, BORDER);
        this.getInventory().setItem((3 * 9) + 3, up);
        this.getInventory().setItem((3 * 9) + 4, BORDER.clone());
        this.getInventory().setItem((3 * 9) + 5, down);
        GUIUtil.drawLine(this.getInventory(), (3 * 9) + 6, 3, BORDER);

        GUIUtil.fillRow(this.getInventory(), 4, BORDER);

        this.getInventory().setItem((5 * 9), OK.clone());
        GUIUtil.drawLine(this.getInventory(), (5 * 9) + 1, 7, BORDER);
        this.getInventory().setItem((5 * 9) + 8, RESET.clone());
    }

    @Override
    public boolean click(InventoryClickEvent e) {
        if(e.getCurrentItem() == null){
            return true;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aOK"))){
            GUIManager.getInstance().openGUI(this.player, new GUIProduct(state));
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&aIncrease Priority"))){
            this.state.priority = this.state.priority + (e.getClick() == ClickType.RIGHT ? 10 : 1);
            updateDisplay();
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cDecrease Priority"))){
            int newPriority = this.state.priority - (e.getClick() == ClickType.RIGHT ? 10 : 1);
            if(newPriority >= 0){
                this.state.priority = newPriority;
                updateDisplay();
            }
        }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(I18N.translate("&cReset Priority"))){
            this.state.priority = 0;
            updateDisplay();
        }

        return true;
    }

    private void updateDisplay() {
        ItemStack is = this.getInventory().getItem((1 * 9) + 4);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(I18N.translate("&bPriority: %0%", this.state.priority));
        is.setItemMeta(im);

        is = this.getInventory().getItem((3 * 9) + 3);
        im = is.getItemMeta();
        List<String> lore = im.getLore();
        lore.set(0, I18N.translate("&bPriority: %0%", this.state.priority));
        im.setLore(lore);
        is.setItemMeta(im);

        is = this.getInventory().getItem((3 * 9) + 5);
        im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
    }

    @Override
    public void close() {
    }

    @Override
    public void open(Player player) {
        this.player = player;
    }
}
