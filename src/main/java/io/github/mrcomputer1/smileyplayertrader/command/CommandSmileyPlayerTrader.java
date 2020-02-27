package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIListItems;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSmileyPlayerTrader implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("smileyplayertrader.command"))
            return false;

        if(args.length == 0){
            if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("useGuiManager", true)) {
                GUIManager.getInstance().openGUI((Player)sender, new GUIListItems(0));
            }else {
                sender.sendMessage(I18N.translate("&bSmiley Player Trader by Mrcomputer1 and sc15. Version %0%.",
                        SmileyPlayerTrader.getInstance().getDescription().getVersion()));
                sender.sendMessage(I18N.translate("&eType /spt help for help!"));
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("version")){
                sender.sendMessage(I18N.translate("&bSmiley Player Trader by Mrcomputer1 and sc15. Version %0%.",
                        SmileyPlayerTrader.getInstance().getDescription().getVersion()));
            }else if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(I18N.translate("&e&lSmiley Player Trader - Help"));
                sender.sendMessage(I18N.translate("&f/spt add [username] &e- Add a new product"));
                sender.sendMessage(I18N.translate("&f/spt list [username] &e- List all products"));
                sender.sendMessage(I18N.translate("&f/spt remove <id> &e- Remove a product"));
                sender.sendMessage(I18N.translate("&f/spt setcost <id> [material] [count] &e- Set cost of a product"));
                sender.sendMessage(I18N.translate("&f/spt setcost2 <id> [material] [count] &e- Set second cost of a product"));
                sender.sendMessage(I18N.translate("&f/spt setproduct <id> [material] [count] &e- Set the product itself"));
                sender.sendMessage(I18N.translate("&f/spt enable <id> &e- Enable the product"));
                sender.sendMessage(I18N.translate("&f/spt disable <id> &e- Disable the product"));
                sender.sendMessage(I18N.translate("&f/spt version &e- Get version"));
                sender.sendMessage(I18N.translate("&e&lSmiley Player Trader - Help"));
            }else if(args[0].equalsIgnoreCase("add")){
                Commands.add((Player)sender, null);
            }else if(args[0].equalsIgnoreCase("list")){
                Commands.list((Player)sender, null);
            }else if(args[0].equalsIgnoreCase("remove")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt remove <id>"));
            }else if(args[0].equalsIgnoreCase("setcost")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt setcost <id> [material] [count]"));
            }else if(args[0].equalsIgnoreCase("setcost2")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt setcost2 <id> [material] [count]"));
            }else if(args[0].equalsIgnoreCase("setproduct")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt setproduct <id> [material] [count]"));
            }else if(args[0].equalsIgnoreCase("enable")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt enable <id>"));
            }else if(args[0].equalsIgnoreCase("disable")){
                sender.sendMessage(I18N.translate("&cBad Syntax! &f/spt disable <id>"));
            }else{
                sender.sendMessage(I18N.translate("&cUnknown sub-command! Use &f/spt help &cfor a list of valid commands!"));
            }
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("add")){
                Commands.add((Player)sender, args[1]);
            }else if(args[0].equalsIgnoreCase("list")){
                Commands.list((Player)sender, args[1]);
            }else if(args[0].equalsIgnoreCase("remove")){
                try {
                    Commands.remove((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setcost")){
                try {
                    Commands.setCost((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setcost2")){
                try {
                    Commands.setCost2((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setproduct")){
                try {
                    Commands.setResult((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("enable")){
                try {
                    Commands.enable((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("disable")){
                try {
                    Commands.disable((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else{
                sender.sendMessage(I18N.translate("&cUnknown sub-command! Use &f/spt help &cfor a list of valid commands!"));
            }
        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("setcost")){
                try {
                    Commands.setCost((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setcost2")){
                try {
                    Commands.setCost2((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setproduct")){
                try {
                    Commands.setResult((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else{
                sender.sendMessage(I18N.translate("&cUnknown sub-command! Use &f/spt help &cfor a list of valid commands!"));
            }
        }else if(args.length >= 4){
            if(args[0].equalsIgnoreCase("setcost")){
                try {
                    Commands.setCost((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]), Integer.parseInt(args[3]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setcost2")){
                try {
                    Commands.setCost2((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]), Integer.parseInt(args[3]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else if(args[0].equalsIgnoreCase("setproduct")){
                try {
                    Commands.setResult((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]), Integer.parseInt(args[3]));
                }catch(NumberFormatException e){
                    sender.sendMessage(I18N.translate("&cInvalid Number!"));
                }
            }else{
                sender.sendMessage(I18N.translate("&cUnknown sub-command! Use &f/spt help &cfor a list of valid commands!"));
            }
        }

        return true;
    }
}
