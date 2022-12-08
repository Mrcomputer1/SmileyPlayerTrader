package io.github.mrcomputer1.smileyplayertrader.gui.framework.component;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class ButtonComponent extends LabelComponent {

    private Function<ClickType, Boolean> onClickEvent;

    public ButtonComponent(int x, int y, Material material, int count, String name, String... lore) {
        super(x, y, material, count, name, lore);
    }

    public void setOnClickEvent(Function<ClickType, Boolean> e){
        this.onClickEvent = e;
    }

    @Override
    public boolean onClick(ClickType type, int x, int y, Player player, ItemStack clickedStack) {
        return this.onClickEvent.apply(type);
    }

}
