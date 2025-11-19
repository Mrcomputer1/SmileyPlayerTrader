package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements ICommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("smileyplayertrader.reload")) {
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorised to do that."));
            return;
        }

        SmileyPlayerTrader.getInstance().reloadConfiguration();
        sender.sendMessage(I18N.translate("&aConfiguration has been reloaded."));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return sender.hasPermission("smileyplayertrader.reload");
    }

}
