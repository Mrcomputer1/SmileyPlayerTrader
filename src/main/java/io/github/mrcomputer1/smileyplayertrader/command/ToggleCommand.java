package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.PlayerConfig;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        Player p = (Player)sender;

        PlayerConfig.Config c = SmileyPlayerTrader.getInstance().getPlayerConfig().getMutablePlayer(p);
        c.tradeToggle = !c.tradeToggle;
        SmileyPlayerTrader.getInstance().getPlayerConfig().updatePlayer(p, c);

        if(c.tradeToggle) {
            sender.sendMessage(I18N.translate("&aToggled trading on."));
        }else{
            sender.sendMessage(I18N.translate("&aToggled trading &coff."));
        }
    }
}
