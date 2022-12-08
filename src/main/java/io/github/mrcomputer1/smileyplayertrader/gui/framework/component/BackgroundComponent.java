package io.github.mrcomputer1.smileyplayertrader.gui.framework.component;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUI;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIComponent;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackgroundComponent extends GUIComponent {

    private final ItemStack stack;

    private Material material;
    private int count;
    private String name;
    private final List<String> lore = new ArrayList<>();

    public BackgroundComponent(int x, int y, int width, int height, Material material, int count, String name, String... lore) {
        super(x, y, width, height);

        this.material = material;
        this.count = count;
        this.name = name;
        this.lore.addAll(Arrays.asList(lore));

        this.stack = new ItemStack(Material.AIR, 0);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    protected void makeItemStack(ItemStack stack){
        stack.setType(this.material);
        stack.setAmount(this.count);
    }

    protected void makeItemMeta(ItemMeta meta){
        meta.setDisplayName(this.name);
        meta.setLore(this.lore);
    }

    @Override
    public void render(Inventory inventory) {
        this.makeItemStack(this.stack);
        ItemMeta itemMeta = this.stack.getItemMeta();
        assert itemMeta != null;
        this.makeItemMeta(itemMeta);
        this.stack.setItemMeta(itemMeta);

        for(int x = this.x; x < this.x + this.width; x++){
            for(int y = this.y; y < this.y + this.height; y++){
                inventory.setItem(GUI.toSlot(x, y), this.stack.clone());
            }
        }
    }

}
