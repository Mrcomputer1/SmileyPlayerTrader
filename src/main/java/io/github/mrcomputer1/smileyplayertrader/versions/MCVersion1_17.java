package io.github.mrcomputer1.smileyplayertrader.versions;

import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

// 1.17 to 1.17.1
public class MCVersion1_17 implements IMCVersion {
    // Sources: NMS = net.minecraft.server, OBC = org.bukkit.craftbukkit
    // Source_ClassName_Static/Instance_MethodName_ArgTypes...

    // Item Stack
    private Constructor<?> NMS_NBTTagCompound__CONSTRUCTOR_;
    private Method NMS_NBTCompressedStreamTools_Static_a_InputStream;
    private Method NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream;
    private Method NMS_ItemStack_Instance_save_NBTTagCompound;
    private Method NMS_ItemStack_Static_a_NBTTagCompound;
    private Method OBC_CraftItemStack_Static_asCraftMirror_ItemStack;
    private Method OBC_CraftItemStack_Static_asNMSCopy_ItemStack;

    // Merchant
    private Method OBC_CraftMerchant_Instance_getMerchant_;
    private Method NMS_IMerchant_Instance_getOffers_;
    private Method NMS_MerchantRecipe_Instance_setSpecialPrice_int;
    private Method NMS_MerchantRecipe_Instance_getSpecialPrice_;
    private Method NMS_MerchantRecipeList_Instance_clear_;
    private Method NMS_MerchantRecipeList_Instance_add_MerchantRecipe;
    private Method OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe;
    private Method OBC_CraftMerchantRecipe_Instance_toMinecraft_;
    private Method OBC_CraftInventoryMerchant_Instance_getInventory_;
    private Method NMS_InventoryMerchant_Instance_getRecipe_;

    public MCVersion1_17(){
        try {
            // Item Stack
            Class<?> NMS_NBTTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
            this.NMS_NBTTagCompound__CONSTRUCTOR_ = NMS_NBTTagCompound.getConstructor();

            Class<?> NMS_NBTCompressedStreamTools = Class.forName("net.minecraft.nbt.NBTCompressedStreamTools");
            this.NMS_NBTCompressedStreamTools_Static_a_InputStream = NMS_NBTCompressedStreamTools.getMethod("a", InputStream.class);
            this.NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream =
                    NMS_NBTCompressedStreamTools.getMethod("a", NMS_NBTTagCompound, OutputStream.class);

            Class<?> NMS_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
            this.NMS_ItemStack_Instance_save_NBTTagCompound = NMS_ItemStack.getMethod("save", NMS_NBTTagCompound);
            this.NMS_ItemStack_Static_a_NBTTagCompound = NMS_ItemStack.getMethod("a", NMS_NBTTagCompound);

            Class<?> OBC_CraftItemStack = Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack");
            this.OBC_CraftItemStack_Static_asCraftMirror_ItemStack = OBC_CraftItemStack.getMethod("asCraftMirror", NMS_ItemStack);
            this.OBC_CraftItemStack_Static_asNMSCopy_ItemStack = OBC_CraftItemStack.getMethod("asNMSCopy", ItemStack.class);

            // Merchant
            Class<?> OBC_CraftMerchant = Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchant");
            this.OBC_CraftMerchant_Instance_getMerchant_ = OBC_CraftMerchant.getMethod("getMerchant");

            Class<?> NMS_IMerchant = Class.forName("net.minecraft.world.item.trading.IMerchant");
            this.NMS_IMerchant_Instance_getOffers_ = NMS_IMerchant.getMethod("getOffers");

            Class<?> NMS_MerchantRecipe = Class.forName("net.minecraft.world.item.trading.MerchantRecipe");
            this.NMS_MerchantRecipe_Instance_setSpecialPrice_int = NMS_MerchantRecipe.getMethod("setSpecialPrice", int.class);
            this.NMS_MerchantRecipe_Instance_getSpecialPrice_ = NMS_MerchantRecipe.getMethod("getSpecialPrice");

            Class<?> NMS_MerchantRecipeList = Class.forName("net.minecraft.world.item.trading.MerchantRecipeList");
            this.NMS_MerchantRecipeList_Instance_clear_ = NMS_MerchantRecipeList.getMethod("clear");
            this.NMS_MerchantRecipeList_Instance_add_MerchantRecipe = NMS_MerchantRecipeList.getMethod("add", Object.class);

            Class<?> OBC_CraftMerchantRecipe = Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchantRecipe");
            this.OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe = OBC_CraftMerchantRecipe.getMethod("fromBukkit", org.bukkit.inventory.MerchantRecipe.class);
            this.OBC_CraftMerchantRecipe_Instance_toMinecraft_ = OBC_CraftMerchantRecipe.getMethod("toMinecraft");

            Class<?> OBC_CraftInventoryMerchant = Class.forName("org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryMerchant");
            this.OBC_CraftInventoryMerchant_Instance_getInventory_ = OBC_CraftInventoryMerchant.getMethod("getInventory");

            Class<?> NMS_InventoryMerchant = Class.forName("net.minecraft.world.inventory.InventoryMerchant");
            this.NMS_InventoryMerchant_Instance_getRecipe_ = NMS_InventoryMerchant.getMethod("getRecipe");
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
            Object nmsis = OBC_CraftItemStack_Static_asNMSCopy_ItemStack.invoke(null, itemStack);
            Object tagCompound = NMS_NBTTagCompound__CONSTRUCTOR_.newInstance();
            NMS_ItemStack_Instance_save_NBTTagCompound.invoke(nmsis, tagCompound);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NMS_NBTCompressedStreamTools_Static_a_NBTTagCompound_OutputStream.invoke(null, tagCompound, baos);
            return baos.toByteArray();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setRecipesOnMerchant(Merchant merchant, List<MerchantRecipe> recipes) throws InvocationTargetException {
        try {
            Object im = OBC_CraftMerchant_Instance_getMerchant_.invoke(merchant);
            Object offers = NMS_IMerchant_Instance_getOffers_.invoke(im);
            NMS_MerchantRecipeList_Instance_clear_.invoke(offers);

            for(MerchantRecipe recipe : recipes){
                Object cmr = OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe.invoke(null, recipe);
                Object mr = OBC_CraftMerchantRecipe_Instance_toMinecraft_.invoke(cmr);
                NMS_MerchantRecipe_Instance_setSpecialPrice_int.invoke(mr, recipe.getSpecialPrice());
                NMS_MerchantRecipeList_Instance_add_MerchantRecipe.invoke(offers, mr);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSpecialCountForRecipe(MerchantInventory inventory) throws InvocationTargetException{
        try {
            Object im = OBC_CraftInventoryMerchant_Instance_getInventory_.invoke(inventory);
            Object mr = NMS_InventoryMerchant_Instance_getRecipe_.invoke(im);
            return (int) NMS_MerchantRecipe_Instance_getSpecialPrice_.invoke(mr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
