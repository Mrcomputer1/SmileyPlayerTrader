package io.github.mrcomputer1.smileyplayertrader.util.item.stocklocations;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class StockLocations {

    private static final Map<String, IStockLocation> registeredStockLocations = new HashMap<>();
    private static final NoOpStockLocation NO_OP_STOCK_LOCATION = new NoOpStockLocation();

    static {
        registerStockLocation("inventory", new InventoryStockLocation());
        registerStockLocation("enderchest", new EnderChestStockLocation());
        registerStockLocation("itemstorage", new ItemStorageStockLocation());
        registerStockLocation("shulkerbox", new ShulkerBoxStockLocation(false));
        registerStockLocation("shulkerbox_enderchest", new ShulkerBoxStockLocation(true));
    }

    public static IStockLocation getStockLocation(String id){
        return registeredStockLocations.get(id);
    }

    public static void registerStockLocation(String id, IStockLocation stockLocation){
        registeredStockLocations.put(id, stockLocation);
    }

    public static List<IStockLocation> getActiveStockLocations(){
        List<String> locations = SmileyPlayerTrader.getInstance().getConfiguration().getStockLocations();

        ArrayList<IStockLocation> stockLocations = new ArrayList<>();

        for(String location : locations){
            IStockLocation stockLocation = getStockLocation(location);
            if(stockLocation == null){
                SmileyPlayerTrader.getInstance().getLogger().warning("Invalid stock location '" + location + "'");
                continue;
            }

            stockLocations.add(stockLocation);
        }

        if(stockLocations.size() == 0){
            stockLocations.add(NO_OP_STOCK_LOCATION);
            SmileyPlayerTrader.getInstance().getLogger().severe("No stock locations are active!");
        }

        return Collections.unmodifiableList(stockLocations);
    }

    public static boolean canTradeWithPlayer(OfflinePlayer player){
        for(IStockLocation location : getActiveStockLocations()){
            if(location.isAvailable(player))
                return true;
        }
        return false;
    }

}
