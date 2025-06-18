package io.github.mrcomputer1.smileyplayertrader.versions;

import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MCVersion1_21_R5 implements IMCVersion {
    // Sources: NMS = net.minecraft.server, OBC = org.bukkit.craftbukkit
    // Source_ClassName_Static/Instance_MethodName_ArgTypes...

    private Object registryAccess;
    private Object nbtOps;
    private Object itemStackCodec;

    // NMS: net.minecraft.nbt.NbtAccounter -> NBTReadLimiter
    private Method NMS_NbtAccounter_Static_unlimitedHeap_;

    // NMS: net.minecraft.nbt.NbtIo -> NBTCompressedStreamTools
    private Method NMS_NbtIo_Static_readCompressed_InputStream_NbtAccounter;
    private Method NMS_NbtIo_Static_writeCompressed_CompoundTag_OutputStream;

    // NMS: net.minecraft.world.item.ItemStack -> ItemStack
    private Method NMS_ItemStack_Instance_isEmpty_;

    // NMS: com.mojang.serialization.Codec
    private Method NMS_Codec_Instance_encodeStart_DynamicOps_Object;
    private Method NMS_Codec_Instance_parse_DynamicOps_Tag;

    // NMS: net.minecraft.core.HolderLookup$Provider -> HolderLookup$a
    private Method NMS_HolderLookup$Provider_Instance_createSerializationContext_DynamicOps;

    // NMS: com.mojang.serialisation.DataResult
    private Method NMS_DataResult_Instance_getOrThrow_;

    // OBC: org.bukkit.craftbukkit._.inventory.CraftItemStack
    private Method OBC_CraftItemStack_Static_asCraftMirror_ItemStack;
    private Method OBC_CraftItemStack_Static_asNMSCopy_ItemStack;

    // OB: org.bukkit.inventory.MerchantRecipe
    private Field OB_MerchantRecipe_Instance_recipe;

    // OBC: org.bukkit.craftbukkit._.inventory.CraftMerchant
    private Method OBC_CraftMerchant_Instance_getMerchant_;

    // NMS: net.minecraft.world.item.trading.Merchant -> IMerchant
    private Method NMS_Merchant_Instance_getOffers_;
    
    // NMS: net.minecraft.world.item.trading.MerchantOffer -> MerchantRecipe
    private Method NMS_MerchantOffer_Instance_setSpecialPriceDiff_int;
    private Method NMS_MerchantOffer_Instance_getSpecialPriceDiff_;

    // NMS: net.minecraft.world.item.trading.MerchantOffers -> MerchantRecipeList
    private Method NMS_MerchantOffers_Instance_clear_;
    private Method NMS_MerchantOffers_Instance_add_MerchantOffer;

    // OBC: org.bukkit.craftbukkit._.inventory.CraftMerchantRecipe
    private Method OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe;
    private Method OBC_CraftMerchantRecipe_Instance_toMinecraft_;

    // OBC: org.bukkit.craftbukkit._.inventory.CraftInventoryMerchant
    private Method OBC_CraftInventoryMerchant_Instance_getInventory_;

    // NMS: net.minecraft.world.inventory.MerchantContainer -> InventoryMerchant
    private Method NMS_MerchantContainer_Instance_getActiveOffer_;

    public MCVersion1_21_R5(World world){
        try {
            /*
             * Classes
             */
            Class<?> NMS_DynamicOps = Class.forName("com.mojang.serialization.DynamicOps");
            Class<?> NMS_NbtOps = Class.forName("net.minecraft.nbt.DynamicOpsNBT");
            Class<?> NMS_HolderLookup$Provider = Class.forName("net.minecraft.core.HolderLookup$a");
            Class<?> NMS_Level = Class.forName("net.minecraft.world.level.World");
            Class<?> NMS_CompoundTag = Class.forName("net.minecraft.nbt.NBTTagCompound");
            Class<?> NMS_NbtAccounter = Class.forName("net.minecraft.nbt.NBTReadLimiter");
            Class<?> NMS_NbtIo = Class.forName("net.minecraft.nbt.NBTCompressedStreamTools");
            Class<?> NMS_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
            Class<?> NMS_Codec = Class.forName("com.mojang.serialization.Codec");
            Class<?> NMS_DataResult = Class.forName("com.mojang.serialization.DataResult");
            Class<?> NMS_Merchant = Class.forName("net.minecraft.world.item.trading.IMerchant");
            Class<?> NMS_MerchantOffer = Class.forName("net.minecraft.world.item.trading.MerchantRecipe");
            Class<?> NMS_MerchantOffers = Class.forName("net.minecraft.world.item.trading.MerchantRecipeList");
            Class<?> NMS_MerchantContainer = Class.forName("net.minecraft.world.inventory.InventoryMerchant");

            Class<?> OB_MerchantRecipe = Class.forName("org.bukkit.inventory.MerchantRecipe");
            Class<?> OBC_CraftWorld = Class.forName("org.bukkit.craftbukkit.v1_21_R5.CraftWorld");
            Class<?> OBC_CraftItemStack = Class.forName("org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack");
            Class<?> OBC_CraftMerchant = Class.forName("org.bukkit.craftbukkit.v1_21_R5.inventory.CraftMerchant");
            Class<?> OBC_CraftMerchantRecipe = Class.forName("org.bukkit.craftbukkit.v1_21_R5.inventory.CraftMerchantRecipe");
            Class<?> OBC_CraftInventoryMerchant = Class.forName("org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryMerchant");

            /*
             * Fields and Methods
             */
            // net.minecraft.nbt.NbtOps
            // Static Field: NMS: NbtOps.INSTANCE
            Field NMS_NbtOps_Static_INSTANCE = NMS_NbtOps.getField("a");

            // net.minecraft.core.HolderLookup$Provider
            // Instance Method: NMS: HolderLookup$Provider.createSerializationContext(DynamicOps)
            this.NMS_HolderLookup$Provider_Instance_createSerializationContext_DynamicOps =
                    NMS_HolderLookup$Provider.getMethod("a", NMS_DynamicOps);

            // org.bukkit.craftbukkit._.CraftWorld
            // Instance Method: OBC: CraftWorld.getHandle()
            Method OBC_CraftWorld_Instance_getHandle_ = OBC_CraftWorld.getMethod("getHandle");

            // net.minecraft.world.level.Level
            // Instance Method: NMS: Level.registryAccess()
            Method NMS_Level_Instance_registryAccess_ = NMS_Level.getMethod("K_");

            // net.minecraft.nbt.NbtAccounter
            // Static Method: NMS: NbtAccounter.unlimitedHeap()
            this.NMS_NbtAccounter_Static_unlimitedHeap_ = NMS_NbtAccounter.getMethod("a");

            // net.minecraft.nbt.NbtIo
            // Static Method: NMS: NbtIo.readCompressed(InputStream, NbtAccounter)
            this.NMS_NbtIo_Static_readCompressed_InputStream_NbtAccounter =
                    NMS_NbtIo.getMethod("a", InputStream.class, NMS_NbtAccounter);
            // Static Method: NMS: NbtIo.writeCompressed(CompoundTag, OutputStream)
            this.NMS_NbtIo_Static_writeCompressed_CompoundTag_OutputStream =
                    NMS_NbtIo.getMethod("a", NMS_CompoundTag, OutputStream.class);

            // net.minecraft.world.item.ItemStack
            // Static Field: NMS: ItemStack.CODEC
            Field NMS_ItemStack_Static_CODEC = NMS_ItemStack.getField("b");
            // Instance Method: NMS: ItemStack.isEmpty()
            this.NMS_ItemStack_Instance_isEmpty_ = NMS_ItemStack.getMethod("f");

            // com.mojang.serialization.Codec
            // Instance Method: NMS: Codec.encodeStart(DynamicOps, Object)
            this.NMS_Codec_Instance_encodeStart_DynamicOps_Object =
                    NMS_Codec.getMethod("encodeStart", NMS_DynamicOps, Object.class);
            // Instance Method: NMS: Codec.parse(DynamicOps, Tag/Object)
            this.NMS_Codec_Instance_parse_DynamicOps_Tag =
                    NMS_Codec.getMethod("parse", NMS_DynamicOps, Object.class);

            // com.mojang.serialization.DataResult
            // Instance Method: NMS: DataResult.getOrThrow()
            this.NMS_DataResult_Instance_getOrThrow_ = NMS_DataResult.getMethod("getOrThrow");

            // org.bukkit.craftbukkit._.inventory.CraftItemStack
            // Static Method: OBC: CraftItemStack.asCraftMirror(NMS: ItemStack)
            this.OBC_CraftItemStack_Static_asCraftMirror_ItemStack =
                    OBC_CraftItemStack.getMethod("asCraftMirror", NMS_ItemStack);
            // Static Method: OBC: CraftItemStack.asNMSCopy(ItemStack)
            this.OBC_CraftItemStack_Static_asNMSCopy_ItemStack =
                    OBC_CraftItemStack.getMethod("asNMSCopy", ItemStack.class);

            // org.bukkit.inventory.MerchantRecipe
            // Instance Field: OB: MerchantRecipe.result
            this.OB_MerchantRecipe_Instance_recipe = OB_MerchantRecipe.getDeclaredField("result");
            this.OB_MerchantRecipe_Instance_recipe.setAccessible(true);

            // org.bukkit.craftbukkit._.inventory.CraftMerchant
            // Instance Method: OBC: CraftMerchant.getMerchant()
            this.OBC_CraftMerchant_Instance_getMerchant_ = OBC_CraftMerchant.getMethod("getMerchant");

            // net.minecraft.world.item.trading.Merchant
            // Instance Method: NMS: Merchant.getOffers()
            this.NMS_Merchant_Instance_getOffers_ = NMS_Merchant.getMethod("gJ");

            // net.minecraft.world.item.trading.MerchantOffer
            // Instance Method: NMS: MerchantOffer.setSpecialPriceDiff(int)
            this.NMS_MerchantOffer_Instance_setSpecialPriceDiff_int = NMS_MerchantOffer.getMethod("b", int.class);
            // Instance Method: NMS: MerchantOffer.getSpecialPriceDiff()
            this.NMS_MerchantOffer_Instance_getSpecialPriceDiff_ = NMS_MerchantOffer.getMethod("o");

            // net.minecraft.world.item.trading.MerchantOffers
            // Instance Method: NMS: MerchantOffers.clear()
            this.NMS_MerchantOffers_Instance_clear_ = NMS_MerchantOffers.getMethod("clear");
            // Instance Method: NMS: MerchantOffers.add(MerchantOffer)
            this.NMS_MerchantOffers_Instance_add_MerchantOffer =
                    NMS_MerchantOffers.getMethod("add", Object.class);

            // org.bukkit.craftbukkit._.inventory.CraftMerchantRecipe
            // Static Method: OBC: CraftMerchantRecipe.fromBukkit(MerchantRecipe)
            this.OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe =
                    OBC_CraftMerchantRecipe.getMethod("fromBukkit", org.bukkit.inventory.MerchantRecipe.class);
            // Instance Method: OBC: CraftMerchantRecipe.toMinecraft()
            this.OBC_CraftMerchantRecipe_Instance_toMinecraft_ = OBC_CraftMerchantRecipe.getMethod("toMinecraft");

            // org.bukkit.craftbukkit._.inventory.CraftInventoryMerchant
            // Instance Method: OBC: CraftInventoryMerchant.getInventory()
            this.OBC_CraftInventoryMerchant_Instance_getInventory_ =
                    OBC_CraftInventoryMerchant.getMethod("getInventory");

            // net.minecraft.world.inventory.MerchantContainer
            // Instance Method: NMS: MerchantContainer.getActiveOffer()
            this.NMS_MerchantContainer_Instance_getActiveOffer_ = NMS_MerchantContainer.getMethod("g");

            /*
             * Constants
             */
            // this.nbtOps = NbtOps.INSTANCE
            this.nbtOps = NMS_NbtOps_Static_INSTANCE.get(null);

            // Object nmsWorld = CraftWorld.getHandle(world)
            Object nmsWorld = OBC_CraftWorld_Instance_getHandle_.invoke(world);

            // this.registryAccess = nmsWorld.registryAccess()
            this.registryAccess = NMS_Level_Instance_registryAccess_.invoke(nmsWorld);

            // this.itemStackCodec = ItemStack.CODEC
            this.itemStackCodec = NMS_ItemStack_Static_CODEC.get(null);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException {
        try {
            // NbtAccounter readLimiter = NbtAccounter.unlimitedHeap()
            Object readLimiter = NMS_NbtAccounter_Static_unlimitedHeap_.invoke(null);
            // CompoundTag tagCompound = NbtIo.readCompressed(new ByteArrayInputStream(array), readLimiter)
            Object tagCompound = NMS_NbtIo_Static_readCompressed_InputStream_NbtAccounter.invoke(null, new ByteArrayInputStream(array), readLimiter);

            // DynamicOps dynamicOps = registryAccess.createSerializationContext(nbtOps)
            Object dynamicOps =
                    NMS_HolderLookup$Provider_Instance_createSerializationContext_DynamicOps.invoke(registryAccess, nbtOps);

            // DataResult dataResult = itemStackCodec.parse(dynamicOps, tagCompound)
            Object dataResult =
                    NMS_Codec_Instance_parse_DynamicOps_Tag.invoke(itemStackCodec, dynamicOps, tagCompound);

            // NMS:ItemStack nmsis = dataResult.getOrThrow()
            Object nmsis = NMS_DataResult_Instance_getOrThrow_.invoke(dataResult);
            // CraftItemStack cis = CraftItemStack.asCraftMirror(nmsis)
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
            // NMS:ItemStack nmsis = CraftItemStack.asNMSCopy(itemStack)
            Object nmsis = OBC_CraftItemStack_Static_asNMSCopy_ItemStack.invoke(null, itemStack);

            // if (nmsis.isEmpty())
            if ((boolean) NMS_ItemStack_Instance_isEmpty_.invoke(nmsis))
                throw new IllegalStateException("Empty items cannot be encoded.");

            // DynamicOps dynamicOps = registryAccess.createSerializationContext(nbtOps)
            Object dynamicOps =
                    NMS_HolderLookup$Provider_Instance_createSerializationContext_DynamicOps.invoke(registryAccess, nbtOps);

            // DataResult dataResult = itemStackCodec.encodeStart(dynamicOps, nmsis)
            Object dataResult =
                    NMS_Codec_Instance_encodeStart_DynamicOps_Object.invoke(itemStackCodec, dynamicOps, nmsis);

            // CompoundTag tagCompound = dataResult.getOrThrow()
            Object tagCompound = NMS_DataResult_Instance_getOrThrow_.invoke(dataResult);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // NbtIo.writeCompressed(tagCompound, baos)
            NMS_NbtIo_Static_writeCompressed_CompoundTag_OutputStream.invoke(null, tagCompound, baos);
            return baos.toByteArray();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setRecipesOnMerchant(Merchant merchant, List<MerchantRecipe> recipes) throws InvocationTargetException {
        try {
            // Merchant im = CraftMerchant.getMerchant(merchant)
            Object im = OBC_CraftMerchant_Instance_getMerchant_.invoke(merchant);
            // MerchantOffers offers = im.getOffers()
            Object offers = NMS_Merchant_Instance_getOffers_.invoke(im);
            // offers.clear()
            NMS_MerchantOffers_Instance_clear_.invoke(offers);

            for(MerchantRecipe recipe : recipes){
                // CraftMerchantRecipe cmr = CraftMerchantRecipe.fromBukkit(recipe)
                Object cmr = OBC_CraftMerchantRecipe_Static_fromBukkit_MerchantRecipe.invoke(null, recipe);
                // MerchantOffer mr = cmr.toMinecraft()
                Object mr = OBC_CraftMerchantRecipe_Instance_toMinecraft_.invoke(cmr);
                // mr.setSpecialPriceDiff(recipe.getSpecialPrice())
                NMS_MerchantOffer_Instance_setSpecialPriceDiff_int.invoke(mr, recipe.getSpecialPrice());
                // offers.add(mr)
                NMS_MerchantOffers_Instance_add_MerchantOffer.invoke(offers, mr);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSpecialCountForRecipe(MerchantInventory inventory) throws InvocationTargetException{
        try {
            // MerchantContainer im = inventory.getInventory()
            Object im = OBC_CraftInventoryMerchant_Instance_getInventory_.invoke(inventory);
            // MerchantOffer mr = im.getActiveOffer()
            Object mr = NMS_MerchantContainer_Instance_getActiveOffer_.invoke(im);
            // return mr.getSpecialPriceDiff()
            return (int) NMS_MerchantOffer_Instance_getSpecialPriceDiff_.invoke(mr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ItemStack getMerchantRecipeOriginalResult(org.bukkit.inventory.MerchantRecipe merchantRecipe) {
        try {
            // return (ItemStack) merchantRecipe.recipe
            return (ItemStack) OB_MerchantRecipe_Instance_recipe.get(merchantRecipe);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
