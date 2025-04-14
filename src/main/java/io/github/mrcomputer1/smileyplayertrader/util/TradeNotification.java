package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class TradeNotification {

    private static boolean isMessageCached = false;
    private static boolean isMessageEnabled = false;
    private static boolean isMessageDefault = false;

    public static void sendNewTradeNotification(OfflinePlayer merchant, ItemStack item) {
        String message = SmileyPlayerTrader.getInstance().getConfiguration().getSendNotificationOnNewTrade();

        if (!isMessageCached) {
            isMessageCached = true;
            isMessageEnabled = !message.equalsIgnoreCase("false");
            isMessageDefault = message.equalsIgnoreCase("default");
        }

        if (isMessageEnabled) {
            if (isMessageDefault) {
                message = I18N.translate("&a%0% is now selling %1%.", merchant.getName(), item.getType().toString());
            } else {
                message = ChatColor.translateAlternateColorCodes('&', message);
                //noinspection DataFlowIssue
                message = message.replace("%MERCHANT%", merchant.getName());
                message = message.replace("%ITEM_TYPE%", item.getType().toString());
            }

            Bukkit.broadcastMessage(message);
        }
    }

}
