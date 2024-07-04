package io.github.mrcomputer1.smileyplayertrader.versions;

import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IMCVersion {

    ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException;
    byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException;

    void setRecipesOnMerchant(Merchant merchant, List<MerchantRecipe> recipes) throws InvocationTargetException;
    int getSpecialCountForRecipe(MerchantInventory inventory) throws InvocationTargetException;

    ItemStack getMerchantRecipeOriginalResult(org.bukkit.inventory.MerchantRecipe merchantRecipe);

}
