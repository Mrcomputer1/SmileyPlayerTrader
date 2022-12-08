package io.github.mrcomputer1.smileyplayertrader.gui.framework.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ItemGridComponent extends GUIComponent {

    private BiFunction<ClickType, ItemStack, Boolean> onClickEvent;

    private final List<ItemStack> items = new ArrayList<>();

    public ItemGridComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setOnClickEvent(BiFunction<ClickType, ItemStack, Boolean> onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    @Override
    public void render(Inventory inventory) {
        int stackIndex = 0;
        for(int y = this.y; y < this.y + this.height; y++){
            for(int x = this.x; x < this.x + this.width; x++){
                if(stackIndex >= this.items.size())
                    return;
                inventory.setItem(GUI.toSlot(x, y), this.items.get(stackIndex++));
            }
        }
    }

    @Override
    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack) {
        return this.onClickEvent.apply(type, clickedStack);
    }

}
