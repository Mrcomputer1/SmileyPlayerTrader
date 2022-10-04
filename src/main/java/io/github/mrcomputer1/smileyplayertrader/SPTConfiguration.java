package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SPTConfiguration {

    private final Configuration config;

    public SPTConfiguration(Configuration config){
        this.config = config;
    }

    public List<String> getDisabledWorlds(){
        return this.config.getStringList("disabledWorlds");
    }

    public List<String> getStockLocations(){
        return this.config.getStringList("stockLocations");
    }

    public boolean getAutoThanks(){
        return this.config.getBoolean("autoThanks", true);
    }

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

}
