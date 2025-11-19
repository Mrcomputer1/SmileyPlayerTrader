package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.command.CommandSender;

public class UnlimitedSupplyCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("smileyplayertrader.unlimitedsupply")) {
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorised to do that."));
            return;
        }

        if(args.length < 1) {
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt unlimitedsupply <id>"));
            return;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e) {
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        if(CommandUtil.isNotAuthorized(sender, id)) {
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.TOGGLE_UNLIMITED_SUPPLY, id);

        sender.sendMessage(I18N.translate("&aToggled unlimited supply."));
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return sender.hasPermission("smileyplayertrader.unlimitedsupply");
    }
}
