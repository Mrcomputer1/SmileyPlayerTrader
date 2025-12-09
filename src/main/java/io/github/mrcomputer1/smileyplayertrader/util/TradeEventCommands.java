package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TradeEventCommands {

    private static void executeCommand(String command, OfflinePlayer merchant, Player customer, ItemStack product, ItemStack cost, ItemStack cost2) {
        boolean onlyIfNoCost2 = command.contains("%ONLY_IF_NO_COST2%");
        boolean onlyIfCost2 = command.contains("%COST2_TYPE%") || command.contains("%COST2_NAME%") || command.contains("%COST2_AMOUNT%");

        if (cost2 == null && onlyIfCost2) // skip command if this command should only run if there is a cost2 and there isn't one.
            return;
        if (cost2 != null && onlyIfNoCost2) // skip command if this command should only run if there is no cost2 and there is one.
            return;

        command = command.replace("%MERCHANT%", merchant.getName() == null ? "null" : merchant.getName());
        command = command.replace("%CUSTOMER%", customer.getName());
        command = command.replace("%PRODUCT_TYPE%", product.getType().name());
        String productItemName = VersionSupport.getPreferredItemName(product.getItemMeta());
        command = command.replace("%PRODUCT_NAME%", productItemName == null ? product.getType().toString() : productItemName);
        command = command.replace("%PRODUCT_AMOUNT%", Integer.toString(product.getAmount()));
        command = command.replace("%COST_TYPE%", cost.getType().name());
        String costItemName = VersionSupport.getPreferredItemName(cost.getItemMeta());
        command = command.replace("%COST_NAME%", cost.getType().name());
        command = command.replace("%COST_AMOUNT%", Integer.toString(cost.getAmount()));
        command = command.replace("%ONLY_IF_NO_COST2%", "");

        if (onlyIfCost2) {
            command = command.replace("%COST2_TYPE%", cost2.getType().name());
            String cost2ItemName = VersionSupport.getPreferredItemName(cost2.getItemMeta());
            command = command.replace("%COST2_NAME%", cost2ItemName == null ? cost2.getType().toString() : cost2ItemName);
            command = command.replace("%COST2_AMOUNT%", Integer.toString(cost2.getAmount()));
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void executeCommands(OfflinePlayer merchant, Player customer, ItemStack product, ItemStack cost, ItemStack cost2) {
        List<String> commands = SmileyPlayerTrader.getInstance().getConfiguration().getOnTradeCompleteCommands();

        for (String command : commands) {
            executeCommand(command, merchant, customer, product, cost, cost2);
        }
    }

}
