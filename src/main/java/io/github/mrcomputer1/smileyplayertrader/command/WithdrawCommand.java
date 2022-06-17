package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class WithdrawCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        if(!SmileyPlayerTrader.getInstance().getConfig().getBoolean("itemStorage.enable", true)){
            sender.sendMessage(I18N.translate("&cItem storage is not enabled."));
            return;
        }

        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt withdraw <id> [limit]"));
            return;
        }

        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        Player p = (Player) sender;

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)) {
            if(set.next()){

                int limit = Integer.MAX_VALUE;
                if(args.length >= 2) {
                    try {
                        limit = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(I18N.translate("&cInvalid Number!"));
                    }
                }

                int countAvailable = set.getInt("stored_product");

                if(limit > countAvailable){
                    limit = countAvailable;
                }

                if(limit <= 0){
                    sender.sendMessage(I18N.translate("&cYou do not have enough of that product."));
                    return;
                }

                ItemStack stack = VersionSupport.byteArrayToItemStack(set.getBytes("product"));

                int itemAmount = limit * stack.getAmount();
                stack.setAmount(itemAmount);

                Map<Integer, ItemStack> errs = p.getInventory().addItem(stack);
                for(ItemStack is : errs.values()){
                    p.getWorld().dropItem(p.getLocation(), is);
                }

                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.CHANGE_STORED_PRODUCT, -limit, id);

                sender.sendMessage(I18N.translate("&aWithdrew %0% of %1%.", itemAmount, stack.getType()));
            }
        } catch (SQLException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
