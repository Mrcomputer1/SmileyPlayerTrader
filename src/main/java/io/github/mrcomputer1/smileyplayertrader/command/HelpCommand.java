package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;

public class HelpCommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(I18N.translate("&e&lSmiley Player Trader - Help"));
        sender.sendMessage(I18N.translate("&f/spt add [username] &e- Add a new product"));
        sender.sendMessage(I18N.translate("&f/spt list [username] &e- List all products"));
        sender.sendMessage(I18N.translate("&f/spt remove <id> &e- Remove a product"));
        sender.sendMessage(I18N.translate("&f/spt setcost <id> [material] [count] &e- Set cost of a product"));
        sender.sendMessage(I18N.translate("&f/spt setcost2 <id> (none|[material] [count]) &e- Set second cost of a product"));
        sender.sendMessage(I18N.translate("&f/spt setproduct <id> [material] [count] &e- Set the product itself"));
        sender.sendMessage(I18N.translate("&f/spt enable <id> &e- Enable the product"));
        sender.sendMessage(I18N.translate("&f/spt disable <id> &e- Disable the product"));
        sender.sendMessage(I18N.translate("&f/spt show <id> &e- Show a product"));
        sender.sendMessage(I18N.translate("&f/spt hide <id> &e- Hide a product"));
        sender.sendMessage(I18N.translate("&f/spt trade <player> &e- Remote trade with a player"));
        sender.sendMessage(I18N.translate("&f/spt discount <id> [amount] &e- Set discount on a product"));
        sender.sendMessage(I18N.translate("&f/spt version &e- Get version"));
        sender.sendMessage(I18N.translate("&e&lSmiley Player Trader - Help"));
    }
}
