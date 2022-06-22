package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIListItems;
import io.github.mrcomputer1.smileyplayertrader.gui.GUIManager;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandSmileyPlayerTrader implements TabExecutor {
    private Map<String, ICommand> commands = new HashMap<>();

    public CommandSmileyPlayerTrader(){
        this.commands.put("help", new HelpCommand());
        this.commands.put("version", new VersionCommand());
        this.commands.put("add", new AddCommand());
        this.commands.put("list", new ListCommand());
        this.commands.put("remove", new RemoveCommand());
        this.commands.put("setcost", new SetCostCommand());
        this.commands.put("setcost2", new SetCost2Command());
        this.commands.put("setproduct", new SetProductCommand());
        this.commands.put("enable", new EnableCommand());
        this.commands.put("show", new EnableCommand());
        this.commands.put("disable", new DisableCommand());
        this.commands.put("toggle", new ToggleCommand());
        this.commands.put("releasecombatlock", new ReleaseCombatLockCommand());
        this.commands.put("trade", new TradeCommand());
        this.commands.put("hide", new HideCommand());
        this.commands.put("discount", new DiscountCommand());
        this.commands.put("openguifor", new OpenGUIForCommand());
        this.commands.put("priority", new PriorityCommand());
        this.commands.put("preview", new PreviewCommand());
        this.commands.put("deposit", new DepositCommand());
        this.commands.put("withdraw", new WithdrawCommand());
        this.commands.put("collect", new CollectCommand());
        this.commands.put("hidewhenout", new HideWhenOutCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("smileyplayertrader.command"))
            return false;

        if(args.length == 0){
            if(SmileyPlayerTrader.getInstance().getConfig().getBoolean("useGuiManager", true)) {
                if(!(sender instanceof Player)){
                    sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
                    return true;
                }
                GUIManager.getInstance().openGUI((Player)sender, new GUIListItems(0));
            }else {
                sender.sendMessage(I18N.translate("&bSmiley Player Trader by Mrcomputer1 and Semisol. Version %0%.",
                        SmileyPlayerTrader.getInstance().getDescription().getVersion()));
                sender.sendMessage(I18N.translate("&eType /spt help for help!"));
            }
            return true;
        }

        if(this.commands.containsKey(args[0].toLowerCase())){
            List<String> arg = new ArrayList<>(Arrays.asList(args));
            arg.remove(0);
            this.commands.get(args[0].toLowerCase()).onCommand(sender, arg.toArray(new String[0]));
        }else{
            sender.sendMessage(I18N.translate("&cUnknown sub-command! Use &f/spt help &cfor a list of valid commands!"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], this.commands.keySet(), tab);
            Collections.sort(tab);
            return tab;
        }else{
            return new ArrayList<>();
        }
    }
}
