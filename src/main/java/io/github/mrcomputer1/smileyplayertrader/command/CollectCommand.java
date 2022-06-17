package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CollectCommand implements ICommand{
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

        Player p = (Player) sender;

        if(args.length >= 1){
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

            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)) {
                if(set.next()){
                    ItemStack cost = VersionSupport.byteArrayToItemStack(set.getBytes("cost1"));

                    byte[] cost2Bytes = set.getBytes("cost2");
                    ItemStack cost2 = cost2Bytes == null ? null : VersionSupport.byteArrayToItemStack(cost2Bytes);

                    int amount = set.getInt("stored_cost");

                    if(amount <= 0){
                        sender.sendMessage(I18N.translate("&cYou have no earnings to collect."));
                        return;
                    }

                    cost.setAmount((cost.getAmount() - set.getInt("special_price")) * amount);

                    Map<Integer, ItemStack> errs = p.getInventory().addItem(cost);
                    for(ItemStack is : errs.values()){
                        p.getWorld().dropItem(p.getLocation(), is);
                    }

                    if(cost2 != null) {
                        cost2.setAmount(cost2.getAmount() * amount);
                        errs = p.getInventory().addItem(cost2);
                        for(ItemStack is : errs.values()){
                            p.getWorld().dropItem(p.getLocation(), is);
                        }
                    }

                    SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_STORED_COST, 0, id);

                    sender.sendMessage(I18N.translate("&aCollected earnings."));
                }
            } catch (SQLException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }else{
            ItemUtil.collectEarnings(p);
        }
    }
}
