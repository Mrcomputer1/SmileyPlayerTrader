package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.command.CommandSender;

public class HideWhenOutCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt hidewhenout <id> <yes|no|true|false>"));
            return;
        }

        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        boolean hide;
        switch(args[1].toLowerCase()){
            case "yes":
            case "true":
                hide = true;
                break;
            case "no":
            case "false":
                hide = false;
                break;
            default:
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt hidewhenout <id> <yes|no|true|false>"));
                return;
        };

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_HIDE_ON_OUT_OF_STOCK, hide, id);
        sender.sendMessage(I18N.translate("&aUpdated hide on out of stock."));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return true;
    }
}
