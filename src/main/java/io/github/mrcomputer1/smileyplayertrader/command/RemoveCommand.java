package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt remove <id>"));
            return;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)) {
            if(set.next()){
                if(set.getInt("stored_product") > 0 || set.getInt("stored_cost") > 0 || set.getInt("stored_cost2") > 0){
                    sender.sendMessage(I18N.translate("&cYou must withdraw all stored product and earnings before deleting the product."));
                    return;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.DELETE_PRODUCT, id);
        sender.sendMessage(I18N.translate("&2Deleted product!"));
    }
}
