package io.github.mrcomputer1.smileyplayertrader.versions;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantRecipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class VersionSupport {

    private static class VersionSupportMeta {
        public final Callable<Boolean> isSupported;
        public final Supplier<? extends IMCVersion> versionSupplier;

        public VersionSupportMeta(Callable<Boolean> isSupported, Supplier<? extends IMCVersion> versionSupplier){
            this.isSupported = isSupported;
            this.versionSupplier = versionSupplier;
        }
    }

    private IMCVersion version;

    private static final List<VersionSupportMeta> supportedVersions = new ArrayList<>();

    static {
        String bukkitVersion = Bukkit.getBukkitVersion();

        // 1.15 - 1.15.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.15").matcher(bukkitVersion).find(),
                MCVersion1_15::new
        );

        // 1.16 - 1.16.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16(\\.1)?-").matcher(bukkitVersion).find(),
                MCVersion1_16::new
        );

        // 1.16.2 - 1.16.3
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16\\.[23]-").matcher(bukkitVersion).find(),
                MCVersion1_16_R2::new
        );

        // 1.16.4 - 1.16.5
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.16\\.[45]-").matcher(bukkitVersion).find(),
                MCVersion1_16_R3::new
        );

        // 1.17 - 1.17.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.17").matcher(bukkitVersion).find(),
                MCVersion1_17::new
        );

        // 1.18 - 1.18.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.18(\\.1)?-").matcher(bukkitVersion).find(),
                MCVersion1_18::new
        );

        // 1.18.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.18\\.2-").matcher(bukkitVersion).find(),
                MCVersion1_18_R2::new
        );

        // 1.19
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19-").matcher(bukkitVersion).find(),
                MCVersion1_19::new
        );

        // 1.19.1 - 1.19.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.[12]-").matcher(bukkitVersion).find(),
                MCVersion1_19_1::new
        );

        // 1.19.3
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.3-").matcher(bukkitVersion).find(),
                MCVersion1_19_R2::new
        );

        // 1.19.4
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.19\\.4-").matcher(bukkitVersion).find(),
                MCVersion1_19_R3::new
        );

        // 1.20 - 1.20.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20(\\.1)?-").matcher(bukkitVersion).find(),
                MCVersion1_20::new
        );

        // 1.20.2
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20\\.2-").matcher(bukkitVersion).find(),
                MCVersion1_20_R2::new
        );

        // 1.20.3 - 1.20.4
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20\\.[34]-").matcher(bukkitVersion).find(),
                MCVersion1_20_R3::new
        );

        // 1.20.5 - 1.20.6
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.20\\.[56]-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_20_R4(Bukkit.getWorlds().get(0))
        );

        // 1.21 - 1.21.1
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21(\\.[1])?-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21(Bukkit.getWorlds().get(0))
        );

        // 1.21.2 - 1.21.3
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.[23]-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R2(Bukkit.getWorlds().get(0))
        );

        // 1.21.4
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.4-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R3(Bukkit.getWorlds().get(0))
        );

        // 1.21.5
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.5-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R4(Bukkit.getWorlds().get(0))
        );

        // 1.21.6 - 1.21.8
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.[678]-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R5(Bukkit.getWorlds().get(0))
        );

        // 1.21.9 - 1.21.10
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.(9|10)-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R6(Bukkit.getWorlds().get(0))
        );

        // 1.21.11
        registerSupportedVersion(
                () -> Pattern.compile("^1\\.21\\.(1[1-9])-").matcher(bukkitVersion).find(),
                () -> new MCVersion1_21_R7(Bukkit.getWorlds().get(0))
        );
    }

    public static void registerSupportedVersion(Callable<Boolean> isSupported, Supplier<? extends IMCVersion> versionSupplier){
        supportedVersions.add(new VersionSupportMeta(isSupported, versionSupplier));
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

    public static ItemStack getMerchantRecipeOriginalResult(org.bukkit.inventory.MerchantRecipe merchantRecipe) {
        return getBoundVersion().getMerchantRecipeOriginalResult(merchantRecipe);
    }

    public static String getPreferredItemName(ItemMeta itemMeta) {
        return getBoundVersion().getPreferredItemName(itemMeta);
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
                    this.version = version.versionSupplier.get();
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
