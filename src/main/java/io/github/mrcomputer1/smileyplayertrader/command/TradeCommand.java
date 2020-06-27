package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.MerchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;

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

        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt trade <name>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null || !target.isOnline()){
            sender.sendMessage(I18N.translate("&cYou cannot trade with offline players."));
            return;
        }

        Merchant merchant = MerchantUtil.buildMerchant(target);
        ((Player)sender).getPlayer().openMerchant(merchant, true);
        target.sendMessage(I18N.translate("&e%0% is now trading with you.", sender.getName()));
    }
}
