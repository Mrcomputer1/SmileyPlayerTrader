package io.github.mrcomputer1.smileyplayertrader.gui.framework.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SlotComponent extends GUIComponent {

    private final Consumer<ItemStack> onUpdateStack;
    private final Supplier<ItemStack> item;

    private Supplier<Boolean> canModify;

    private boolean isChanged = false;

    private Inventory inventory;

    public SlotComponent(int x, int y, Supplier<ItemStack> stack, Consumer<ItemStack> onUpdateStack) {
        super(x, y, 1, 1);
        this.item = stack;
        this.onUpdateStack = onUpdateStack;
    }

    public void setCanModify(Supplier<Boolean> canModify) {
        this.canModify = canModify;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public ItemStack getItem(){
        return this.inventory.getItem(GUI.toSlot(this.x, this.y));
    }

    public void setItem(ItemStack is){
        this.inventory.setItem(GUI.toSlot(this.x, this.y), is);
    }

    public void updateItem(Player player, boolean shouldReturn){
        this.isChanged = false;
        ItemStack item = this.inventory.getItem(GUI.toSlot(this.x, this.y));
        if(item != null && item.getType().isAir())
            item = null;
        this.onUpdateStack.accept(item == null ? null : item.clone());
        if(item != null && shouldReturn)
            player.getInventory().addItem(item.clone());
    }

    public void setChanged(boolean changed){
        this.isChanged = changed;
    }

    @Override
    public void render(Inventory inventory) {
        this.inventory = inventory;

        ItemStack stack = this.item.get().clone();
        if(stack.getType().isAir())
            this.isChanged = true;

        this.inventory.setItem(GUI.toSlot(x, y), stack);
    }

    @Override
    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack) {
        if(type != ClickType.LEFT && type != ClickType.RIGHT)
            return false;

        if(this.canModify != null && !this.canModify.get())
            return false;

        if(!this.isChanged){
            this.inventory.setItem(GUI.toSlot(this.x, this.y), null);
            this.isChanged = true;
            return false;
        }else return true;
    }

    @Override
    public void onParentClose(Player player) {
        if(this.isChanged)
            updateItem(player, true);
    }

}
