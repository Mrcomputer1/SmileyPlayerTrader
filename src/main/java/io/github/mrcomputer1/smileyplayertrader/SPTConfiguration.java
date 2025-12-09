package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SPTConfiguration {

    private final Configuration config;

    public SPTConfiguration(Configuration config){
        this.config = config;
    }

    // Allowed Worlds
    public enum EnumAllowedWorldsMode {
        BLACKLIST("blacklist"),
        WHITELIST("whitelist");

        private static final Map<String, EnumAllowedWorldsMode> allowedWorldModes = new HashMap<>();

        static {
            for (EnumAllowedWorldsMode mode : values()) {
                allowedWorldModes.put(mode.id.toLowerCase(), mode);
            }
        }

        public static EnumAllowedWorldsMode getById(String id) {
            if (id == null)
                return BLACKLIST;
            EnumAllowedWorldsMode mode = allowedWorldModes.get(id.toLowerCase());
            return mode == null ? EnumAllowedWorldsMode.BLACKLIST : mode;
        }

        private final String id;

        EnumAllowedWorldsMode(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public EnumAllowedWorldsMode getAllowedWorldsMode() {
        return EnumAllowedWorldsMode.getById(this.config.getString("allowedWorlds.mode", EnumAllowedWorldsMode.BLACKLIST.getId()));
    }

    public List<String> getAllowedWorldsList() {
        if (this.config.contains("allowedWorlds.worlds", true))
            return this.config.getStringList("allowedWorlds.worlds");

        // Legacy config property support
        return this.config.getStringList("disabledWorlds");
    }

    public boolean hasLegacyDisabledWorldsAndAllowedWorldsConfig() {
        return this.config.contains("disabledWorlds", true) && this.config.contains("allowedWorlds.worlds", true);
    }
    // End Allowed Worlds

    public List<String> getStockLocations(){
        return this.config.getStringList("stockLocations");
    }

    public boolean isUseOnlyOneStockLocation() {
        return this.config.getBoolean("useOnlyOneStockLocation", false);
    }

    // Auto Thanks
    public enum EnumAutoThanks{
        PLAYER_CHAT("player_chat"),
        SYSTEM_CHAT("system_chat"),
        NONE("none");

        private static final Map<String, EnumAutoThanks> autoThanksModes = new HashMap<>();

        static{
            for(EnumAutoThanks mode : values()){
                autoThanksModes.put(mode.id.toLowerCase(), mode);
            }
        }

        public static EnumAutoThanks getById(String id){
            if(id == null)
                return EnumAutoThanks.SYSTEM_CHAT;
            EnumAutoThanks mode = autoThanksModes.get(id.toLowerCase());
            return mode == null ? EnumAutoThanks.SYSTEM_CHAT : mode;
        }

        private final String id;

        EnumAutoThanks(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public enum EnumAutoThanksMessageTarget{
        EVERYONE("everyone"),
        CUSTOMER("customer"),
        MERCHANT("merchant"),
        INVOLVED("involved");

        private static final Map<String, EnumAutoThanksMessageTarget> autoThanksTargets = new HashMap<>();

        static{
            for(EnumAutoThanksMessageTarget target : values()){
                autoThanksTargets.put(target.id.toLowerCase(), target);
            }
        }

        public static EnumAutoThanksMessageTarget getById(String id){
            if(id == null)
                return EnumAutoThanksMessageTarget.EVERYONE;
            EnumAutoThanksMessageTarget target = autoThanksTargets.get(id.toLowerCase());
            return target == null ? EVERYONE : target;
        }

        private final String id;

        EnumAutoThanksMessageTarget(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public EnumAutoThanks getAutoThanksMode(){
        // Legacy config property support
        if(this.config.isBoolean("autoThanks"))
            return this.config.getBoolean("autoThanks", true) ? EnumAutoThanks.SYSTEM_CHAT : EnumAutoThanks.NONE;

        return EnumAutoThanks.getById(this.config.getString("autoThanks.mode", EnumAutoThanks.SYSTEM_CHAT.getId()));
    }

    public String getAutoThanksMessage(){
        String message = this.config.getString("autoThanks.message", "default");
        //noinspection ConstantConditions
        if(message.equalsIgnoreCase("default"))
            return null;
        else
            return message;
    }

    public EnumAutoThanksMessageTarget getAutoThanksMessageTarget(){
        return EnumAutoThanksMessageTarget.getById(this.config.getString("autoThanks.target", "everyone"));
    }
    // End Auto Thanks

    public String getCurrentLanguage(){
        return this.config.getString("currentLanguage", "en_us");
    }

    // Start Database
    public String getDatabaseType(){
        return this.config.getString("database.type", "sqlite");
    }

    public String getDatabaseFile(){
        return this.config.getString("database.file", "database.db");
    }

    public String getDatabasePrefix(){
        return this.config.getString("database.prefix", "spt");
    }

    public String getDatabaseHost(){
        return this.config.getString("database.host");
    }

    public int getDatabasePort(){
        return this.config.getInt("database.port", 3306);
    }

    public String getDatabaseName(){
        return this.config.getString("database.name");
    }

    public String getDatabaseUsername(){
        return this.config.getString("database.username");
    }

    public String getDatabasePassword(){
        return this.config.getString("database.password");
    }
    // End Database

    public boolean getCheckForUpdatesEnabled(){
        return this.config.getBoolean("checkForUpdates", true);
    }

    // Start Bug Warner
    public boolean getCheckForBugsEnabled(){
        return this.config.getBoolean("checkForBugs.check", false);
    }

    public boolean getCheckForBugsShouldDisable(){
        return this.config.getBoolean("checkForBugs.disable", false);
    }
    // End Bug Warner

    public boolean getUseGuiManager(){
        return this.config.getBoolean("useGuiManager", true);
    }

    public boolean getDisableRightClickTrading(){
        return this.config.getBoolean("disableRightClickTrading", false);
    }

    // Start Item Storage
    public boolean getItemStorageEnabled(){
        return this.config.getBoolean("itemStorage.enable", false);
    }

    public int getItemStorageProductStorageLimit(){
        return this.config.getInt("itemStorage.productStorageLimit", -1);
    }

    public boolean getItemStorageNotifyUncollectedEarningsEnabled(){
        return this.config.getBoolean("itemStorage.notifyUncollectedEarningsOnLogin", true);
    }
    // End Item Storage

    // Start Out of Stock Behaviour
    public enum EnumOutOfStockBehaviour{
        SHOW_BY_DEFAULT("showByDefault"),
        HIDE_BY_DEFAULT("hideByDefault"),
        SHOW("show"),
        HIDE("hide");

        private static final Map<String, EnumOutOfStockBehaviour> behaviours = new HashMap<>();

        static {
            for(EnumOutOfStockBehaviour behaviour : values()){
                behaviours.put(behaviour.id.toLowerCase(), behaviour);
            }
        }

        public static EnumOutOfStockBehaviour getById(String id){
            if(id == null)
                return EnumOutOfStockBehaviour.SHOW_BY_DEFAULT;
            EnumOutOfStockBehaviour behaviour = behaviours.get(id.toLowerCase());
            return behaviour == null ? EnumOutOfStockBehaviour.SHOW_BY_DEFAULT : behaviour;
        }

        private final String id;

        EnumOutOfStockBehaviour(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }

    public EnumOutOfStockBehaviour getOutOfStockBehaviour(){
        return EnumOutOfStockBehaviour.getById(this.config.getString("outOfStockBehaviour", EnumOutOfStockBehaviour.SHOW_BY_DEFAULT.getId()));
    }
    // End Out of Stock Behaviour

    public String getSendNotificationOnNewTrade() {
        return this.config.getString("sendNotificationOnNewTrade", "false");
    }

    public boolean getDoesUnlimitedSupplyEarn() {
        return this.config.getBoolean("doesUnlimitedSupplyEarn", true);
    }


    public boolean getRequireItemInHandWhileUsingCreateCommand() {
        return this.config.getBoolean("requireItemInHandWhileUsingCreateCommand", false);
    }

    // Start Purchase Cost Comparison
    public enum EnumPurchaseCostComparison {
        STRICT("strict"),
        VANILLA("vanilla");

        private static final Map<String, EnumPurchaseCostComparison> methods = new HashMap<>();

        static {
            for (EnumPurchaseCostComparison method : values()) {
                methods.put(method.id.toLowerCase(), method);
            }
        }

        public static EnumPurchaseCostComparison getById(String id){
            if(id == null)
                return EnumPurchaseCostComparison.STRICT;
            EnumPurchaseCostComparison behaviour = methods.get(id.toLowerCase());
            return behaviour == null ? EnumPurchaseCostComparison.STRICT : behaviour;
        }

        private final String id;

        EnumPurchaseCostComparison(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public EnumPurchaseCostComparison getPurchaseCostComparison() {
        return EnumPurchaseCostComparison.getById(this.config.getString("purchaseCostComparison", EnumPurchaseCostComparison.STRICT.getId()));
    }
    // End Purchase Cost Comparison

    public boolean getDisableVaultOfflinePermissionChecking() {
        return this.config.getBoolean("disableVaultOfflinePermissionChecking", false);
    }

    // Start Auto Combat Lock
    public boolean getAutoCombatLockEnabled(){
        return this.config.getBoolean("autoCombatLock.enabled", true);
    }

    public int getAutoCombatLockLength(){
        return this.config.getInt("autoCombatLock.combatLockLength", 30);
    }

    public boolean getAutoCombatLockNeverShowNotice(){
        return this.config.getBoolean("autoCombatLock.neverShowNotice", false);
    }
    // End Auto Combat Lock

    // Start Cooldown
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getCooldownEnabled() {
        return this.config.getBoolean("cooldown.enabled", false);
    }

    public int getCooldownLength() {
        return this.config.getInt("cooldown.seconds", 10);
    }
    // End Cooldown

    public List<ItemStack> getPriceQuickSelection(){
        List<?> priceQuickSelection = SmileyPlayerTrader.getInstance().getConfig().getList("priceQuickSelection", new ArrayList<>());
        List<ItemStack> stacks = new ArrayList<>();
        //noinspection ConstantConditions
        for (Object itemObject : priceQuickSelection) {
            try {
                //noinspection unchecked
                LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) itemObject;

                ItemStack stack = ItemUtil.buildConfigurationItem(item);
                if (stack != null)
                    stacks.add(stack);
            }catch (ClassCastException ex){
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to parse price quick selection item.");
            }
        }
        return stacks;
    }

    // Start Price Selector Menu
    public boolean getPriceSelectorMenuAutomaticAddVanilla(){
        return this.config.getBoolean("priceSelectorMenu.automaticAdd.vanilla", true);
    }

    public List<String> getPriceSelectorMenuHiddenItems(){
        return this.config.getStringList("priceSelectorMenu.hiddenItems");
    }

    public List<ItemStack> getPriceSelectorMenuExtraItems(){
        List<?> extraItems = SmileyPlayerTrader.getInstance().getConfig().getList("priceSelectorMenu.extraItems", new ArrayList<>());
        List<ItemStack> stacks = new ArrayList<>();
        //noinspection ConstantConditions
        for (Object itemObject : extraItems) {
            try {
                //noinspection unchecked
                LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) itemObject;

                ItemStack stack = ItemUtil.buildConfigurationItem(item);
                if (stack != null)
                    stacks.add(stack);
            }catch (ClassCastException ex){
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to parse price selector menu extra item.");
            }
        }
        return stacks;
    }

    public List<ItemStack> getPriceSelectorMenuFeaturedItems(){
        List<?> featuredItems = SmileyPlayerTrader.getInstance().getConfig().getList("priceSelectorMenu.featuredItems", new ArrayList<>());
        List<ItemStack> stacks = new ArrayList<>();
        //noinspection ConstantConditions
        for (Object itemObject : featuredItems) {
            try {
                //noinspection unchecked
                LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) itemObject;

                ItemStack stack = ItemUtil.buildConfigurationItem(item);
                if (stack != null)
                    stacks.add(stack);
            }catch (ClassCastException ex){
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to parse price selector menu featured item.");
            }
        }
        return stacks;
    }
    // End Price Selector Menu

    public boolean getDebugSelfTrading(){
        return this.config.getBoolean("debugSelfTrading", false);
    }

    public boolean getDebugI18NAlerts(){
        return this.config.getBoolean("debugI18NAlerts", false);
    }

    public boolean getDebugSQLStatements(){
        return this.config.getBoolean("debugSQLStatements", false);
    }

    public int getDebugHiddenItemsExtraPagesLimit() {
        return this.config.getInt("debugHiddenItemsExtraPagesLimit", -1);
    }

}
