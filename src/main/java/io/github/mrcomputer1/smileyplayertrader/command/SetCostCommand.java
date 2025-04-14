package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.TradeNotification;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SetCostCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // Usage check.
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt setcost <id> [material] [count]"));
            return;
        }

        // Read item ID.
        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch(NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        // Check if authorised to change item.
        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        // Details about the product for new trade notifications.
        boolean wasProductValid = false;
        boolean wasCostValid = false;
        OfflinePlayer owner = null;
        ItemStack productItem = null;

        // Retrieve details about the product.
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id)) {
            if(set.next()){
                // Prevent changing the item if there is a stored cost.
                if(set.getInt("stored_cost") > 0){
                    sender.sendMessage(I18N.translate("&cYou must collect all earnings before changing the cost."));
                    return;
                }

                // Retrieves details about the cost for new trade notifications.
                owner = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("merchant")));
                wasProductValid = set.getBytes("product") != null;
                wasCostValid = set.getBytes("cost1") != null;
                if(wasProductValid)
                    productItem = VersionSupport.byteArrayToItemStack(set.getBytes("product"));
            }
        } catch (SQLException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        try {
            // /spt setcost <id>
            if(args.length == 1) {
                // Must be a player.
                if(!(sender instanceof Player)){
                    sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
                    return;
                }
                Player p = (Player)sender;

                // Check player is holding an item.
                if (p.getInventory().getItemInMainHand().getType().isAir()) {
                    sender.sendMessage(I18N.translate("&cYou must be holding an item in your main hand!"));
                    return;
                }

                // Convert item to NBT data.
                byte[] item = VersionSupport.itemStackToByteArray(p.getInventory().getItemInMainHand());

                // Set the cost.
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_COST, item, id);

                // Send success response.
                sender.sendMessage(I18N.translate("&aCost set!"));

                // Send new trade notification if product is valid and cost has become valid.
                if(wasProductValid && (!wasCostValid && item != null))
                    TradeNotification.sendNewTradeNotification(owner, productItem);
            }else if(args.length >= 2){
                // Attempt to read material.
                Material material = Material.matchMaterial(args[1]);
                if(material == null || !material.isItem() || material.isAir()) {
                    sender.sendMessage(I18N.translate("&c%0% isn't a valid item.", args[1]));
                    return;
                }

                // Attempt to read count if it is present. Otherwise, assume 1.
                int count = 1;
                try {
                    count = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                    return;
                }

                // Ensure the count is actually valid for this item.
                if(count < 1 || count > material.getMaxStackSize()){
                    sender.sendMessage(I18N.translate("&cNumber is either too large or too small."));
                    return;
                }

                // Build the ItemStack and convert it to NBT data.
                ItemStack is = new ItemStack(material);
                is.setAmount(count);
                byte[] item = VersionSupport.itemStackToByteArray(is);

                // Set the cost.
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_COST, item, id);

                // Send success response.
                sender.sendMessage(I18N.translate("&aCost set!"));

                // Send new trade notification if product is valid and cost has become valid.
                if(wasProductValid && (!wasCostValid && item != null))
                    TradeNotification.sendNewTradeNotification(owner, productItem);
            }
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
