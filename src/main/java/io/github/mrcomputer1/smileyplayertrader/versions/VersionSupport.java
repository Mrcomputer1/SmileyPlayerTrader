package io.github.mrcomputer1.smileyplayertrader.versions;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class VersionSupport {

    private static class VersionSupportMeta {
        public final Callable<Boolean> isSupported;
        public final Class<?> versionClass;

        public VersionSupportMeta(Callable<Boolean> isSupported, Class<?> versionClass){
            this.isSupported = isSupported;
            this.versionClass = versionClass;

            if(!IMCVersion.class.isAssignableFrom(versionClass))
                throw new IllegalArgumentException("Version class does not implement IMCVersion.");
        }
    }

    private IMCVersion version;

    private static final List<VersionSupportMeta> supportedVersions = new ArrayList<>();

    static {
        // 1.15 - 1.15.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.15").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_15.class
        );

        // 1.16 - 1.16.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16(\\.1)?-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_16.class
        );

        // 1.16.2 - 1.16.3
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16\\.[23]-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_16_R2.class
        );

        // 1.16.4 - 1.16.5
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16\\.[45]-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_16_R3.class
        );

        // 1.17 - 1.17.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.17").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_17.class
        );

        // 1.18 - 1.18.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.18(\\.1)?-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_18.class
        );

        // 1.18.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.18\\.2-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_18_R2.class
        );

        // 1.19
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_19.class
        );

        // 1.19.1 - 1.19.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.[12]-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_19_1.class
        );

        // 1.19.3
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.3-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_19_R2.class
        );

        // 1.19.4
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.4-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_19_R3.class
        );

        // 1.20 - 1.20.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20(\\.1)?-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_20.class
        );

        // 1.20.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20(\\.[2-9])?-").matcher(Bukkit.getBukkitVersion()).find(),
                MCVersion1_20_R2.class
        );
    }

    public static void registerSupportedVersion(Callable<Boolean> isSupported, Class<?> versionClass){
        supportedVersions.add(new VersionSupportMeta(isSupported, versionClass));
    }

    private static IMCVersion getBoundVersion(){
        return SmileyPlayerTrader.getInstance().getVersionSupport().version;
    }

    public static ItemStack byteArrayToItemStack(byte[] array) throws InvocationTargetException {
        return getBoundVersion().byteArrayToItemStack(array);
    }

    public static byte[] itemStackToByteArray(ItemStack itemStack) throws InvocationTargetException {
        return getBoundVersion().itemStackToByteArray(itemStack);
    }

    public static void setRecipesOnMerchant(Merchant merchant, List<MerchantRecipe> recipes) throws InvocationTargetException {
        getBoundVersion().setRecipesOnMerchant(merchant, recipes);
    }

    public static int getSpecialCountForRecipe(MerchantInventory inventory) throws InvocationTargetException {
        return getBoundVersion().getSpecialCountForRecipe(inventory);
    }

    public void bindCompatibleVersion() throws IllegalStateException {
        if(this.version != null)
            throw new IllegalStateException("Already bound version.");

        // Version Logging
        String mcVersion = Bukkit.getBukkitVersion().split("-")[0];
        SmileyPlayerTrader.getInstance().getLogger().info("Bukkit version is '" + Bukkit.getBukkitVersion() + "', detected Minecraft version is '" + mcVersion + "'.");

        // Version Finding
        for (VersionSupportMeta version : supportedVersions){
            try {
                if (version.isSupported.call()) {
                    this.version = (IMCVersion) version.versionClass.getConstructor().newInstance();
                    return;
                }
            } catch (Exception ex){
                SmileyPlayerTrader.getInstance().getLogger().warning("Unexpected exception in version support check, trying next version...");
                ex.printStackTrace();
            }
        }

        throw new IllegalStateException("No supported version was found.");
    }

}
