package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
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

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.FIND_PRODUCTS,
                target.getUniqueId().toString())) {
            while (set.next()) {
                boolean isStocked = true;
                byte[] productb = set.getBytes("product");
                String products = I18N.translate(", Product: UNSET");
                if(productb != null){
                    ItemStack product = MerchantUtil.buildItem(productb);
                    if(target.isOnline() && !ItemUtil.doesPlayerHaveItem(target.getPlayer(), product, set.getLong("id"))){
                        isStocked = false;
                    }

                    String preferredItemName = VersionSupport.getPreferredItemName(product.getItemMeta());
                    if(preferredItemName != null) {
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), preferredItemName);
                    }else{
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), product.getType());
                    }
                }

                byte[] cost1b = set.getBytes("cost1");
                String cost1s = I18N.translate(", Cost 1: UNSET");
                if(cost1b != null){
                    ItemStack cost1 = MerchantUtil.buildItem(cost1b);

                    String preferredItemName = VersionSupport.getPreferredItemName(cost1.getItemMeta());
                    if(preferredItemName != null) {
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), preferredItemName);
                    }else{
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), cost1.getType());
                    }
                }

                byte[] cost2b = set.getBytes("cost2");
                String cost2s = I18N.translate(", Cost 2: UNSET");
                if(cost2b != null) {
                    ItemStack cost2 = MerchantUtil.buildItem(cost2b);

                    String preferredItemName = VersionSupport.getPreferredItemName(cost2.getItemMeta());
                    if(preferredItemName != null) {
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), preferredItemName);
                    }else{
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), cost2.getType());
                    }
                }

                String enabled;
                if(set.getBoolean("enabled")){
                    enabled = I18N.translate(", Enabled: YES");
                }else{
                    enabled = I18N.translate(", Enabled: NO");
                }

                String available;
                if(set.getBoolean("available")){
                    available = I18N.translate(", Available: YES");
                }else{
                    available = I18N.translate(", Available: NO");
                }

                if(set.getBoolean("enabled") && set.getBoolean("available")) {
                    if (isStocked) {
                        sender.sendMessage(I18N.translate("&e - %0% %1% %2% %3% %4% %5%", set.getLong("id"), products, cost1s, cost2s, enabled, available));
                    } else {
                        sender.sendMessage(I18N.translate("&c - [OUT OF STOCK] %0% %1% %2% %3% %4% %5%", set.getLong("id"), products, cost1s, cost2s, enabled, available));
                    }
                }else{
                    sender.sendMessage(I18N.translate("&4 - %0% %1% %2% %3% %4% %5%", set.getLong("id"), products, cost1s, cost2s, enabled, available));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
