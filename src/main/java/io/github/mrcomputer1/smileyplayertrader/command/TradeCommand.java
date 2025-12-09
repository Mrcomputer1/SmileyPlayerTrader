package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.RegionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        if(!sender.hasPermission("smileyplayertrader.trade.remote") || !sender.hasPermission("smileyplayertrader.trade")){
            sender.sendMessage(I18N.translate("&cYou don't have permission to remote trade."));
            return;
        }

        if(!RegionUtil.isAllowedRemote((Player) sender)) {
            sender.sendMessage(I18N.translate("&cYou cannot trade here."));
            return;
        }

        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt trade <name>"));
            return;
        }

        //noinspection deprecation
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        MerchantUtil.openMerchant((Player) sender, target, true, false);
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return sender instanceof Player && sender.hasPermission("smileyplayertrader.trade.remote") && sender.hasPermission("smileyplayertrader.trade");
    }
}
