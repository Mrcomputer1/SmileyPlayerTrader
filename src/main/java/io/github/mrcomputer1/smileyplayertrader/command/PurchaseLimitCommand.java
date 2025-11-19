package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PurchaseLimitCommand implements ICommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt purchaselimit <id> [limit]"));
            return;
        }

        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        int limit;
        if(args.length < 2) {
            limit = -1;
        }else{
            try {
                limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(I18N.translate("&cInvalid Number!"));
                return;
            }
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        if(limit < -1){
            sender.sendMessage(I18N.translate("&cYou cannot have a negative purchase limit."));
            return;
        }

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_PURCHASE_LIMIT, id, limit);
        sender.sendMessage(I18N.translate("&aSet purchase limit."));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return true;
    }

}
