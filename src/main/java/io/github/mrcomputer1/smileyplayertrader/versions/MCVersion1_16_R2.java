package io.github.mrcomputer1.smileyplayertrader.versions;

import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MCVersion1_16_R2 implements IMCVersion {
    // Sources: NMS = net.minecraft.server, OBC = org.bukkit.craftbukkit
    // Source_ClassName_Static/Instance_MethodName_ArgTypes...
    private Constructor NMS_NBTTagCompound__CONSTRUCTOR_;
    private Method NMS_NBTCompressedStreamTools_Static_a_InputStream;
    private Method NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream;
    private Method NMS_ItemStack_Instance_save_NBTTagCompound;
    private Method NMS_ItemStack_Static_a_NBTTagCompound;
    private Method OBC_CraftItemStack_Static_asCraftMirror_ItemStack;
    private Method OBS_CraftItemStack_Static_asNMSCopy_ItemStack;

    public MCVersion1_16_R2(){
        try {
            Class NMS_NBTTagCompound = Class.forName("net.minecraft.server.v1_16_R2.NBTTagCompound");
            this.NMS_NBTTagCompound__CONSTRUCTOR_ = NMS_NBTTagCompound.getConstructor();

            Class NMS_NBTCompressedStreamTools = Class.forName("net.minecraft.server.v1_16_R2.NBTCompressedStreamTools");
            this.NMS_NBTCompressedStreamTools_Static_a_InputStream = NMS_NBTCompressedStreamTools.getMethod("a", InputStream.class);
            this.NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream =
                    NMS_NBTCompressedStreamTools.getMethod("a", NMS_NBTTagCompound, OutputStream.class);

            Class NMS_ItemStack = Class.forName("net.minecraft.server.v1_16_R2.ItemStack");
            this.NMS_ItemStack_Instance_save_NBTTagCompound = NMS_ItemStack.getMethod("save", NMS_NBTTagCompound);
            this.NMS_ItemStack_Static_a_NBTTagCompound = NMS_ItemStack.getMethod("a", NMS_NBTTagCompound);

            Class OBC_CraftItemStack = Class.forName("org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack");
            this.OBC_CraftItemStack_Static_asCraftMirror_ItemStack = OBC_CraftItemStack.getMethod("asCraftMirror", NMS_ItemStack);
            this.OBS_CraftItemStack_Static_asNMSCopy_ItemStack = OBC_CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException{
        try {
            Object tagCompound = NMS_NBTCompressedStreamTools_Static_a_InputStream.invoke(null, new ByteArrayInputStream(array));
            Object nmsis = NMS_ItemStack_Static_a_NBTTagCompound.invoke(null, tagCompound);
            Object cis = OBC_CraftItemStack_Static_asCraftMirror_ItemStack.invoke(null, nmsis);
            return (ItemStack)cis;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException{
        try {
            Object nmsis = OBS_CraftItemStack_Static_asNMSCopy_ItemStack.invoke(null, itemStack);
            Object tagCompound = NMS_NBTTagCompound__CONSTRUCTOR_.newInstance();
            NMS_ItemStack_Instance_save_NBTTagCompound.invoke(nmsis, tagCompound);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream.invoke(null, tagCompound, baos);
            return baos.toByteArray();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
