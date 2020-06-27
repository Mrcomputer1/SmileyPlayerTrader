package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.PlayerConfig;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReleaseCombatLockCommand implements ICommand{
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(I18N.translate("&cYou must be running this command from a player."));
            return;
        }

        Player p = (Player)sender;

        if(SmileyPlayerTrader.getInstance().getPlayerConfig().isLocked(p)) {
            SmileyPlayerTrader.getInstance().getPlayerConfig().releasePlayerLock(p);
            sender.sendMessage(I18N.translate("&aReleased combat lock. Trading is re-enabled."));
        }else{
            sender.sendMessage(I18N.translate("&cYou are not currently combat locked."));
        }
    }
}
