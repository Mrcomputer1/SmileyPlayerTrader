package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SPTConfiguration;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.versions.VersionSupport;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCommand implements ICommand {

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt create [product [amount]] (amount <amount>|" +
                "cost1 <cost> [amount]|" +
                "cost2 <cost> [amount]|" +
                "hidden|" +
                "disabled|" +
                "discount <amount>|" +
                "priority <priority>|" +
                "purchaselimit <limit>|" +
                "hideonoutofstock|" +
                "deposithand|" +
                "unlimitedsupply|" +
                "player <player>)..."));
    }

    @Override
    public void onCommand(CommandSender sender, String[] rawArgs) {
        // Usage Check
        if (rawArgs.length < 1) {
            sendUsage(sender);
            return;
        }

        List<String> args = new ArrayList<>(Arrays.asList(rawArgs));

        // Default parameters
        OfflinePlayer player = sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null;
        Player sendingPlayer = sender instanceof Player ? (Player) sender : null;
        ItemStack cost1 = null;
        ItemStack cost2 = null;
        ItemStack product;
        boolean hidden = false;
        boolean disabled = false;
        int specialPrice = 0;
        int priority = 0;
        int purchaseLimit = -1;
        boolean unlimitedSupply = false;
        boolean depositHand = false;

        SPTConfiguration.EnumOutOfStockBehaviour outOfStockBehaviour = SmileyPlayerTrader.getInstance().getConfiguration().getOutOfStockBehaviour();
        boolean hideOnOutOfStock;
        switch (outOfStockBehaviour){
            case HIDE_BY_DEFAULT:
            case HIDE:
                hideOnOutOfStock = true;
                break;
            case SHOW_BY_DEFAULT:
            case SHOW:
            default:
                hideOnOutOfStock = false;
                break;
        }

        /*
         * Product
         */
        {
            //
            // Material
            //
            Material material = Material.matchMaterial(args.get(0));

            if (material == null) { // Invalid material, use item in hand instead.
                if (sendingPlayer != null) { // Only try to use the item in hand if the command is being executed by a player
                    ItemStack hand = sendingPlayer.getInventory().getItemInMainHand();
                    if (hand.getType().isAir()) { // No item in hand
                        if (!SmileyPlayerTrader.getInstance().getConfiguration().getRequireItemInHandWhileUsingCreateCommand()) {
                            sender.sendMessage(I18N.translate("&cYou must be holding an item in your main hand or specify a valid item!"));
                        } else {
                            sender.sendMessage(I18N.translate("&cYou must be holding an item in your main hand!"));
                        }
                        return;
                    }

                    product = sendingPlayer.getInventory().getItemInMainHand().clone();
                } else { // Can't use the item in the hand of something that doesn't have a hand.
                    sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
                    return;
                }
            } else { // Valid material
                if (!material.isItem() || material.isAir()) { // Verify this is actually an item.
                    sender.sendMessage(I18N.translate("&c%0% isn't a valid item.", args.get(0)));
                    return;
                }

                product = new ItemStack(material);

                // If the item is required to be in your hand to create the product:
                if (SmileyPlayerTrader.getInstance().getConfiguration().getRequireItemInHandWhileUsingCreateCommand()) {
                    if (sendingPlayer != null && !product.isSimilar(sendingPlayer.getInventory().getItemInMainHand())) {
                        sender.sendMessage(I18N.translate("&cYou must be holding a matching item in your main hand!"));
                        return;
                    }
                }

                args.remove(0);
            }

            //
            // Amount
            //
            if (!args.isEmpty()) {
                try {
                    int count = Integer.parseInt(args.get(0));
                    if (count < 1 || count > product.getMaxStackSize()) {
                        sender.sendMessage(I18N.translate("&cNumber is either too large or too small."));
                        return;
                    }

                    args.remove(0);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Options
        while (!args.isEmpty()) {
            String command = args.remove(0).toLowerCase();

            switch (command) {
                /*
                 * Amount
                 */
                case "amount": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    try {
                        int count = Integer.parseInt(args.remove(0));
                        if (count < 1 || count > product.getMaxStackSize()) {
                            sender.sendMessage(I18N.translate("&cNumber is either too large or too small."));
                            return;
                        }

                        product.setAmount(count);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(I18N.translate("&cInvalid Number!"));
                        return;
                    }

                    break;
                }

                /*
                 * Primary Cost
                 */
                case "cost1":
                case "primarycost":
                case "cost": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    Material material = Material.matchMaterial(args.remove(0));
                    if (material == null || !material.isItem() || material.isAir()) {
                        sender.sendMessage(I18N.translate("&c%0% isn't a valid item.", args.get(0)));
                        return;
                    }

                    cost1 = new ItemStack(material);

                    if (!args.isEmpty()) {
                        try {
                            int count = Integer.parseInt(args.get(0));

                            if (count < 1 || count > material.getMaxStackSize()) {
                                sender.sendMessage(I18N.translate("&cNumber is either too large or too small."));
                                return;
                            }

                            cost1.setAmount(count);
                            args.remove(0);
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    break;
                }

                /*
                 * Secondary Cost
                 */
                case "cost2":
                case "secondarycost": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    Material material = Material.matchMaterial(args.remove(0));
                    if (material == null || !material.isItem() || material.isAir()) {
                        sender.sendMessage(I18N.translate("&c%0% isn't a valid item.", args.get(0)));
                        return;
                    }

                    cost2 = new ItemStack(material);

                    if (!args.isEmpty()) {
                        try {
                            int count = Integer.parseInt(args.get(0));

                            if (count < 1 || count > material.getMaxStackSize()) {
                                sender.sendMessage(I18N.translate("&cNumber is either too large or too small."));
                                return;
                            }

                            cost2.setAmount(count);
                            args.remove(0);
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    break;
                }

                /*
                 * Hidden
                 */
                case "hidden": {
                    hidden = true;

                    break;
                }

                /*
                 * Disabled
                 */
                case "disabled": {
                    disabled = true;

                    break;
                }

                /*
                 * Discount
                 */
                case "discount": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    if (cost1 == null) {
                        sender.sendMessage(I18N.translate("&cYou must specify primary cost first."));
                        return;
                    }

                    try {
                        int discount = Integer.parseInt(args.remove(0));
                        int calculatedPrice = -discount + cost1.getAmount();

                        if (calculatedPrice < 1 || calculatedPrice > cost1.getMaxStackSize()) {
                            sender.sendMessage(I18N.translate("&cDiscount would make price too small or too large."));
                            return;
                        }

                        specialPrice = discount;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(I18N.translate("&cInvalid Number!"));
                        return;
                    }

                    break;
                }

                /*
                 * Priority
                 */
                case "priority": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    try {
                        priority = Integer.parseInt(args.remove(0));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(I18N.translate("&cInvalid Number!"));
                        return;
                    }

                    break;
                }

                /*
                 * Purchase Limit
                 */
                case "purchaselimit": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    try {
                        purchaseLimit = Integer.parseInt(args.remove(0));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(I18N.translate("&cInvalid Number!"));
                        return;
                    }

                    break;
                }

                /*
                 * Hide when out of stock
                 */
                case "hideonoutofstock": {
                    hideOnOutOfStock = true;

                    break;
                }

                /*
                 * Deposit hand
                 */
                case "deposithand": {
                    if (!SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageEnabled()){
                        sender.sendMessage(I18N.translate("&cItem storage is not enabled."));
                        return;
                    }

                    depositHand = true;

                    break;
                }

                /*
                 * Unlimited supply
                 */
                case "unlimitedsupply": {
                    if (!sender.hasPermission("smileyplayertrader.unlimitedsupply")) {
                        sender.sendMessage(I18N.translate("&cWhoops! You are not authorised to do that."));
                        return;
                    }

                    unlimitedSupply = true;

                    break;
                }

                /*
                 * Player
                 */
                case "player": {
                    if (args.isEmpty()) {
                        sendUsage(sender);
                        return;
                    }

                    if (!sender.hasPermission("smileyplayertrader.others")){
                        sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
                        return;
                    }

                    //noinspection deprecation
                    player = Bukkit.getOfflinePlayer(args.remove(0));

                    break;
                }

                default: {
                    sender.sendMessage(I18N.translate("&cInvalid option %0%.", command));

                    break;
                }
            }
        }

        if (player == null) {
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        try {
            // Create product
            byte[] stackBytes = VersionSupport.itemStackToByteArray(product);

            byte[] cost1Bytes = cost1 == null ? null : VersionSupport.itemStackToByteArray(cost1);
            byte[] cost2Bytes = cost2 == null ? null : VersionSupport.itemStackToByteArray(cost2);

            long id = SmileyPlayerTrader.getInstance().getStatementHandler().runAndReturnInsertId(
                    StatementHandler.StatementType.ADD_PRODUCT,
                    player.getUniqueId().toString(), stackBytes, cost1Bytes, cost2Bytes,
                    !disabled, !hidden, specialPrice, priority, hideOnOutOfStock
            );

            // Configure product as needed
            if (unlimitedSupply) {
                SmileyPlayerTrader.getInstance().getStatementHandler().run(
                        StatementHandler.StatementType.TOGGLE_UNLIMITED_SUPPLY, id
                );
            }

            SmileyPlayerTrader.getInstance().getStatementHandler().run(
                    StatementHandler.StatementType.SET_PURCHASE_LIMIT, id, purchaseLimit
            );

            // Deposit hand (if enabled)
            if (depositHand && sendingPlayer != null) {
                ItemStack hand = sendingPlayer.getInventory().getItemInMainHand();

                if (hand.isSimilar(product)){
                    int count = hand.getAmount();

                    int limit = SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageProductStorageLimit();
                    if(limit != -1 && count > limit){
                        sender.sendMessage(I18N.translate("&cYou cannot store more than %0% of a product.", limit));
                        return;
                    }

                    // Add to storage
                    SmileyPlayerTrader.getInstance().getStatementHandler().run(
                            StatementHandler.StatementType.CHANGE_STORED_PRODUCT,
                            count, id
                    );

                    // Remove from hand
                    hand.setAmount(hand.getAmount() - count);
                } else {
                    sender.sendMessage(I18N.translate("&cThis item does not match the type of the product so no items were deposited."));
                }
            }

            // Display confirmation
            sender.spigot().sendMessage(I18N.translateComponents(
                    "&aCreated product %0% with ID %1%.", ItemUtil.getItemTextComponent(product),
                    new TextComponent(String.valueOf(id))
            ));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isVisibleInTabComplete(CommandSender sender) {
        return true;
    }

}
