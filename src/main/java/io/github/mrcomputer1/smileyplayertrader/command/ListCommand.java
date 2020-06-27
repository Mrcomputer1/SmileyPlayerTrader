package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.MerchantUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ListCommand implements ICommand{
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

        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.FIND_PRODUCTS,
                target.getUniqueId().toString());

        try {
            while (set.next()) {
                boolean isStocked = true;
                byte[] productb = set.getBytes("product");
                String products = I18N.translate(", Product: UNSET");
                if(productb != null){
                    ItemStack product = MerchantUtil.buildItem(productb);
                    if(target.isOnline() && !ItemUtil.doesPlayerHaveItem(target.getPlayer(), product)){
                        isStocked = false;
                    }
                    if(product.getItemMeta().hasDisplayName()) {
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), product.getItemMeta().getDisplayName());
                    }else{
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), product.getType());
                    }
                }

                byte[] cost1b = set.getBytes("cost1");
                String cost1s = I18N.translate(", Cost 1: UNSET");
                if(cost1b != null){
                    ItemStack cost1 = MerchantUtil.buildItem(cost1b);
                    if(cost1.getItemMeta().hasDisplayName()) {
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), cost1.getItemMeta().getDisplayName());
                    }else{
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), cost1.getType());
                    }
                }

                byte[] cost2b = set.getBytes("cost2");
                String cost2s = I18N.translate(", Cost 2: UNSET");
                if(cost2b != null) {
                    ItemStack cost2 = MerchantUtil.buildItem(cost2b);
                    if(cost2.getItemMeta().hasDisplayName()) {
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), cost2.getItemMeta().getDisplayName());
                    }else{
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), cost2.getType());
                    }
                }
                if(set.getBoolean("enabled")) {
                    if (isStocked) {
                        sender.sendMessage(I18N.translate("&e - %0% %1% %2% %3%, Enabled: YES", set.getLong("id"), products, cost1s, cost2s));
                    } else {
                        sender.sendMessage(I18N.translate("&c - [OUT OF STOCK] %0% %1% %2% %3%, Enabled: YES", set.getLong("id"), products, cost1s, cost2s));
                    }
                }else{
                    sender.sendMessage(I18N.translate("&4 - %0% %1% %2% %3%, Enabled: NO", set.getLong("id"), products, cost1s, cost2s));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
