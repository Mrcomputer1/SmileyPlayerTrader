package io.github.mrcomputer1.smileyplayertrader.versions;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public interface IMCVersion {

    ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException;
    byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException;

}
