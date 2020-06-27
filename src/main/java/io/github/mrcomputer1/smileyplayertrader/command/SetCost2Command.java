package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.CommandUtil;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ReflectionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class SetCost2Command implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt setcost2 <id> [material] [count]"));
            return;
        }

        long id;
        try{
            id = Long.parseLong(args[0]);
        }catch(NumberFormatException e){
            sender.sendMessage(I18N.translate("&cInvalid Number!"));
            return;
        }

        if(CommandUtil.isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        try {
            if(args.length == 1) {
                if(!(sender instanceof Player)){
                    sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
                    return;
                }
                Player p = (Player)sender;

                if (p.getInventory().getItemInMainHand().getType().isAir()) {
                    sender.sendMessage(I18N.translate("&cYou must be holding an item in your main hand!"));
                    return;
                }
                byte[] item = ReflectionUtil.itemStackToByteArray(p.getInventory().getItemInMainHand());

                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_SECONDARY_COST, item, id);

                sender.sendMessage(I18N.translate("&aSecondary cost set!"));
            }else if(args.length >= 2){
                Material material = Material.matchMaterial(args[1]);
                int count = 1;
                try {
                    count = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                    return;
                }

                ItemStack is = new ItemStack(material);
                is.setAmount(count);
                byte[] item = ReflectionUtil.itemStackToByteArray(is);

                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.SET_SECONDARY_COST, item, id);

                sender.sendMessage(I18N.translate("&aSecondary cost set!"));
            }
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
