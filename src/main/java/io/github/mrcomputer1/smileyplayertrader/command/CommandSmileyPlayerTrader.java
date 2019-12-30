package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.AQUA + "Smiley Player Trader by Mrcomputer1 and sc15. Version " +
                    SmileyPlayerTrader.getInstance().getDescription().getVersion() + ".");
            sender.sendMessage(ChatColor.YELLOW + "Type /spt help for help!");
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Smiley Player Trader - Help");
                sender.sendMessage(ChatColor.YELLOW + "/spt add [username] - Add a new prudct");
                sender.sendMessage(ChatColor.YELLOW + "/spt list [username] - List all products");
                sender.sendMessage(ChatColor.YELLOW + "/spt remove <id> - Remove a product");
                sender.sendMessage(ChatColor.YELLOW + "/spt setcost <id> [material] - Set cost of a product");
                sender.sendMessage(ChatColor.YELLOW + "/spt setcost2 <id> [material] - Set second cost of a product");
                sender.sendMessage(ChatColor.YELLOW + "/spt setproduct <id> [material] - Set the product itself");
                sender.sendMessage(ChatColor.YELLOW + "/spt enable <id> - Enable the product");
                sender.sendMessage(ChatColor.YELLOW + "/spt disable <id> - Disable the product");
                sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Smiley Player Trader - Help");
            }else if(args[0].equalsIgnoreCase("add")){
                Commands.add((Player)sender, null);
            }else if(args[0].equalsIgnoreCase("list")){
                Commands.list((Player)sender, null);
            }else if(args[0].equalsIgnoreCase("remove")){
                sender.sendMessage(ChatColor.RED + "Bad Syntax! /spt remove <id>");
            }else if(args[0].equalsIgnoreCase("setcost")){
                sender.sendMessage(ChatColor.RED + "Bad Syntax! /spt setcost <id> [material]");
            }else if(args[0].equalsIgnoreCase("setcost2")){
                sender.sendMessage(ChatColor.RED + "Bad Syntax! /spt setcost <id> [material]");
            }else if(args[0].equalsIgnoreCase("setproduct")){
                sender.sendMessage(ChatColor.RED + "Bad Syntax! /spt setcost <id> [material]");
            }else{
                sender.sendMessage(ChatColor.RED + "Unknown sub-command! Use /spt for a list of valid commands!");
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
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("setcost")){
                try {
                    Commands.setCost((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("setcost2")){
                try {
                    Commands.setCost2((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("setproduct")){
                try {
                    Commands.setResult((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("enable")){
                try {
                    Commands.enable((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("disable")){
                try {
                    Commands.disable((Player) sender, Long.parseLong(args[1]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else{
                sender.sendMessage(ChatColor.RED + "Unknown sub-command! Use /spt for a list of valid commands!");
            }
        }else if(args.length >= 3){
            if(args[0].equalsIgnoreCase("setcost")){
                try {
                    Commands.setCost((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("setcost2")){
                try {
                    Commands.setCost2((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else if(args[0].equalsIgnoreCase("setproduct")){
                try {
                    Commands.setResult((Player) sender, Long.parseLong(args[1]), Material.matchMaterial(args[2]));
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + "Invalid Number!");
                }
            }else{
                sender.sendMessage(ChatColor.RED + "Unknown sub-command! Use /spt for a list of valid commands!");
            }
        }

        return true;
    }
}
