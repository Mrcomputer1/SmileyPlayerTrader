package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;

public class VersionCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(I18N.translate("&bSmiley Player Trader by Mrcomputer1 and sc15. Version %0%.",
                SmileyPlayerTrader.getInstance().getDescription().getVersion()));
    }
}
