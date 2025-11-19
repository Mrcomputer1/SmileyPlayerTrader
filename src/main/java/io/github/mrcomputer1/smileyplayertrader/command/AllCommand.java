package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIAllProducts;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllCommand implements ICommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        if(!SmileyPlayerTrader.getInstance().getConfiguration().getUseGuiManager()){
            sender.sendMessage(I18N.translate("&cGUI functionality is disabled, this command has no effect."));
            return;
        }

        if(!sender.hasPermission("smileyplayertrader.alltradeslist")){
            sender.sendMessage(I18N.translate("&cYou don't have permission to see all trades."));
            return;
        }

        boolean canPurchase = sender.hasPermission("smileyplayertrader.trade.remote") && sender.hasPermission("smileyplayertrader.trade");
        GUIManager.getInstance().openGui((Player) sender, new GUIAllProducts(0, canPurchase));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return sender instanceof Player && sender.hasPermission("smileyplayertrader.alltradeslist");
    }


}
