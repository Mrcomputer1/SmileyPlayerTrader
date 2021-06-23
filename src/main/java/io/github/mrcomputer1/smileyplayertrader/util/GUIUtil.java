package io.github.mrcomputer1.smileyplayertrader.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIUtil {

    public static void fillRow(Inventory inv, int row, ItemStack stack){
        drawLine(inv, row * 9, 9, stack);
    }

    public static void drawLine(Inventory inv, int pos, int size, ItemStack stack){
        for(int i = 0; i < size; i++){
            inv.setItem(pos + i, stack.clone());
        }
    }

    public static void fillStartAndEnd(Inventory inv, int row, ItemStack stack){
        inv.setItem(row * 9, stack.clone());
        inv.setItem((row * 9) + 8, stack.clone());
    }

    public static void spreadItems(Inventory inv, int startCol, int cols, int startRow, int rows, ItemStack[] items){
        int item = 0;
        for(int row = startRow; row < (startRow + rows); row++){
            for(int col = startCol; col < (startCol + cols); col++){
                int pos = (row * 9) + col;
                if(items.length == item){
                    return;
                }
                inv.setItem(pos, items[item]);
                item++;
            }
        }
    }

    public static void spreadItemsCloned(Inventory inv, int startCol, int cols, int startRow, int rows, ItemStack[] items){
        int item = 0;
        for(int row = startRow; row < (startRow + rows); row++){
            for(int col = startCol; col < (startCol + cols); col++){
                int pos = (row * 9) + col;
                if(items.length == item){
                    return;
                }
                if(items[item] == null)
                    continue;
                inv.setItem(pos, items[item].clone());
                item++;
            }
        }
    }

}
