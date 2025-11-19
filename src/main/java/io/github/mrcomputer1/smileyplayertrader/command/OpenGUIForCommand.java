package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIProductList;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class OpenGUIForCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("smileyplayertrader.guiothers")){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorised to open the GUI for others."));
            return;
        }

        if(!SmileyPlayerTrader.getInstance().getConfiguration().getUseGuiManager()){
            sender.sendMessage(I18N.translate("&cGUI functionality is disabled, this command has no effect."));
            return;
        }

        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt openguifor <player>"));
            return;
        }

        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
        if(op == null || !op.isOnline()){
            sender.sendMessage(I18N.translate("&cThat player is not online."));
            return;
        }

        GUIManager.getInstance().openGui(op.getPlayer(), new GUIProductList(op.getPlayer(), op, 0, true));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return sender.hasPermission("smileyplayertrader.guiothers");
    }
}
