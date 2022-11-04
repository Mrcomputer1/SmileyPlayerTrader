package io.github.mrcomputer1.smileyplayertrader.gui.framework;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class GUI {

    //<editor-fold desc="Helper Methods and Constants">
    public static int getX(int slot){
        return slot % 9;
    }

    public static int getY(int slot){
        return slot / 9;
    }

    public static boolean isInGUIArea(int rows, int slot){
        return slot < rows * 9;
    }

    public static int toSlot(int x, int y){
        return x + (y * 9);
    }

    public static ItemStack createItem(Material material, int count, String name){
        ItemStack is = new ItemStack(material, count);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(name);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItemWithLore(Material material, int count, String name, String... lore){
        ItemStack is = new ItemStack(material, count);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItemWithLoreAndModify(Material material, int count, String name, Consumer<ItemStack> stackModify, Consumer<ItemMeta> metaModify, String... lore){
        ItemStack is = new ItemStack(material, count);
        if(stackModify != null)
            stackModify.accept(is);

        ItemMeta im = is.getItemMeta();
        assert im != null;

        if(metaModify != null)
            metaModify.accept(im);

        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));

        is.setItemMeta(im);

        return is;
    }

    public static final ItemStack BACKGROUND = GUI.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ChatColor.RESET.toString());
    //</editor-fold>

    //<editor-fold desc="GUI Initial State">
    private final int rows;
    private final String title;

    private Inventory inventory;
    private Player player;
    //</editor-fold>

    //<editor-fold desc="GUI State">
    private final List<GUIComponent> children = new ArrayList<>();
    private final Map<Integer, GUIComponent> usedSlots = new HashMap<>();

    private boolean doBackgroundFill = true;
    private boolean allowInteractingWithPlayerInventory = false;
    //</editor-fold>

    public GUI(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    protected final void init(Inventory inventory, Player player){
        this.inventory = inventory;
        this.player = player;
        this.render();
    }

    //<editor-fold desc="Getters">
    public final Player getPlayer() {
        return player;
    }

    public final String getTitle(){
        return this.title;
    }

    public final int getRows(){
        return this.rows;
    }
    //</editor-fold>

    //<editor-fold desc="Properties">
    public final void setDoBackgroundFill(boolean doBackgroundFill){
        this.doBackgroundFill = doBackgroundFill;
    }

    public final void setAllowInteractingWithPlayerInventory(boolean allowInteractingWithPlayerInventory){
        this.allowInteractingWithPlayerInventory = allowInteractingWithPlayerInventory;
    }
    //</editor-fold>

    //<editor-fold desc="Children Management">
    public final void addChild(GUIComponent component){
        this.children.add(component);
    }

    public final void removeChild(GUIComponent component){
        this.children.remove(component);
    }
    //</editor-fold>

    public final void refreshComponent(GUIComponent component){
        component.render(this.inventory);
    }

    public final void render(){
        assert this.inventory != null && this.player != null;

        this.inventory.clear();
        this.usedSlots.clear();

        for(GUIComponent child : this.children){
            child.render(this.inventory);

            if(this.doBackgroundFill) {
                for (int x = child.x; x < child.x + child.width; x++) {
                    for (int y = child.y; y < child.y + child.height; y++) {
                        this.usedSlots.put(GUI.toSlot(x, y), child);
                    }
                }
            }
        }

        if(this.doBackgroundFill){
            for(int i = 0; i < this.rows * 9; i++){
                if(!this.usedSlots.containsKey(i))
                    this.inventory.setItem(i, GUI.BACKGROUND.clone());
            }
        }
    }

    //<editor-fold desc="Event Handlers">
    protected boolean onPlayerInventoryClick(ClickType click, int clickSlot, ItemStack clickedStack){
        return true;
    }

    protected void onClose(){
    }
    //</editor-fold>

    //<editor-fold desc="Internal Event Handlers">
    protected final void onInventoryClick(InventoryClickEvent e){
        if(e.getRawSlot() == InventoryView.OUTSIDE){
            e.setCancelled(true);
            return;
        }

        try {
            if (!GUI.isInGUIArea(this.rows, e.getRawSlot())) {
                if (this.allowInteractingWithPlayerInventory) {
                    if (!this.onPlayerInventoryClick(e.getClick(), e.getRawSlot(), e.getCurrentItem()))
                        e.setCancelled(true);
                } else e.setCancelled(true);
            } else {
                if (this.usedSlots.containsKey(e.getRawSlot())) {
                    GUIComponent component = this.usedSlots.get(e.getRawSlot());
                    if (!component.onClick(e.getClick(), GUI.getX(e.getRawSlot()) - component.getX(), GUI.getY(e.getRawSlot()) - component.getY(), this.getPlayer(), e.getCurrentItem()))
                        e.setCancelled(true);
                } else e.setCancelled(true);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            String exceptionName = ex.getClass().getSimpleName();
            if(ex.getCause() != null){
                exceptionName = ex.getCause().getClass().getSimpleName();
            }

            this.player.sendMessage(I18N.translate("&cSomething went wrong (&f%0%&c)!", exceptionName));

            e.setCancelled(true);
        }
    }

    protected final void onInventoryClose(Inventory inventory){
        for(GUIComponent child : this.children)
            child.onParentClose(this.getPlayer());
        this.onClose();
    }
    //</editor-fold>

}
