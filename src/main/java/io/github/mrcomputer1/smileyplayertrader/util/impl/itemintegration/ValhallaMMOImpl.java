package io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import me.athlaeos.valhallammo.item.CustomItemRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class ValhallaMMOImpl implements IItemIntegrationImpl {

    @Override
    public ItemStack getItem(LinkedHashMap<String, Object> item) {
        if (!item.containsKey("id") && !(item.get("id") instanceof String)) {
            SmileyPlayerTrader.getInstance().getLogger().severe("id is not a String");
            return null;
        }

        String id = (String) item.get("id");
        return CustomItemRegistry.getProcessedItem(id);
    }

}
