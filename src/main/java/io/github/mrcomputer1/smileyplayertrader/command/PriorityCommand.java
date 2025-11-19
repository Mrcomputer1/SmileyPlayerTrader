package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.command.CommandSender;

public class PriorityCommand implements ICommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt priority <id> [amount]"));
            return;
        }

        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        int amount;
        if(args.length < 2){
            amount = 0;
        }else{
            try{
                amount = Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                sender.sendMessage(I18N.translate("&cInvalid Number!"));
                return;
            }
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        if(amount < 0){
            sender.sendMessage(I18N.translate("&cYou cannot have a negative priority."));
            return;
        }

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_PRIORITY, amount, id);
        sender.sendMessage(I18N.translate("&aSet priority."));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return true;
    }

}
