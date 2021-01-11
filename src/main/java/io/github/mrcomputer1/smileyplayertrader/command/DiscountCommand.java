package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ReflectionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt discount <id> [amount]"));
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
        if(args.length < 2) {
            amount = 0;
        }else{
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(I18N.translate("&cInvalid Number!"));
                return;
            }
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id);
        try {
            if(set.next()){
                byte[] cost = set.getBytes("cost1");
                ItemStack costIS = ReflectionUtil.byteArrayToItemStack(cost);

                int specialPrice = -amount + costIS.getAmount();
                if(specialPrice < 1 || specialPrice > costIS.getMaxStackSize()){
                    sender.sendMessage(I18N.translate("&cDiscount would make price too small or too large."));
                }else{
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_DISCOUNT, amount, id);
                    sender.sendMessage(I18N.translate("&aSet discount."));
                }
            }
        } catch (SQLException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
