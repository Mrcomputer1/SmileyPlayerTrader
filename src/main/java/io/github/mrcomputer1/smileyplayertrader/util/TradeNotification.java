package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class TradeNotification {

    private static boolean isMessageCached = false;
    private static boolean isMessageEnabled = false;
    private static boolean isMessageDefault = false;

    public static void sendNewTradeNotification(OfflinePlayer merchant, ItemStack item) {
        String messageText = SmileyPlayerTrader.getInstance().getConfiguration().getSendNotificationOnNewTrade();

        if (!isMessageCached) {
            isMessageCached = true;
            isMessageEnabled = !messageText.equalsIgnoreCase("false");
            isMessageDefault = messageText.equalsIgnoreCase("default");
        }

        if (isMessageEnabled) {
            if (isMessageDefault) {
                BaseComponent component = I18N.translateComponents(
                        "&a%0% is now selling %1%.", new TextComponent(merchant.getName()), ItemUtil.getItemTextComponent(item)
                );
                Bukkit.spigot().broadcast(component);
            } else {
                //noinspection DataFlowIssue
                messageText = messageText.replace("%MERCHANT%", merchant.getName());
                messageText = messageText.replace("%ITEM_TYPE%", item.getType().toString());
                messageText = messageText.replace("%ITEM_NAME%", "%0%");

                BaseComponent component = I18N.parseTranslationStringToComponent(
                        messageText, ItemUtil.getItemTextComponent(item)
                );
                Bukkit.spigot().broadcast(component);
            }
        }
    }

}
