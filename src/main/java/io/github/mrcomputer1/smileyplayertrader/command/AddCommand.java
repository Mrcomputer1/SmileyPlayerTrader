package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SPTConfiguration;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if(args.length != 0){
            target = Bukkit.getOfflinePlayer(args[0]);
            if(!sender.hasPermission("smileyplayertrader.others")){
                sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
                return;
            }
        }else{
            if(!(sender instanceof Player)){
                sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
                return;
            }
            target = (Player)sender;
        }

        SPTConfiguration.EnumOutOfStockBehaviour outOfStockBehaviour = SmileyPlayerTrader.getInstance().getConfiguration().getOutOfStockBehaviour();
        boolean hideOnOutOfStock;
        switch (outOfStockBehaviour){
            case HIDE_BY_DEFAULT:
            case HIDE:
                hideOnOutOfStock = true;
                break;
            case SHOW_BY_DEFAULT:
            case SHOW:
            default:
                hideOnOutOfStock = false;
                break;
        }

        long id = SmileyPlayerTrader.getInstance().getStatementHandler().runAndReturnInsertId(
                StatementHandler.StatementType.ADD_PRODUCT,
                target.getUniqueId().toString(), null, null, null, true, true, 0, 0, hideOnOutOfStock
        );

        sender.sendMessage(I18N.translate("&aAdded product %0%. Use &f/spt setcost <id> &aand &f/spt setproduct <id> &awhile holding items!", id));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return true;
    }
}
