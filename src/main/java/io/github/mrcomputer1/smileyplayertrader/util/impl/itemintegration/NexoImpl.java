package io.github.mrcomputer1.smileyplayertrader.util.impl.itemintegration;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Optional;

public class NexoImpl implements IItemIntegrationImpl {

    @Override
    public ItemStack getItem(LinkedHashMap<String, Object> item) {
        if (!item.containsKey("id") && !(item.get("id") instanceof String)) {
            SmileyPlayerTrader.getInstance().getLogger().severe("id is not a String");
            return null;
        }

        String id = (String) item.get("id");

        Optional<ItemBuilder> itemBuilder = NexoItems.optionalItemFromId(id);
        if (!itemBuilder.isPresent()) {
            SmileyPlayerTrader.getInstance().getLogger().severe(id + " is not a valid Nexo item.");
            return null;
        }

        return itemBuilder.get().build();
    }

}
