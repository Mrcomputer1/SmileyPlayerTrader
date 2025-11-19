package io.github.mrcomputer1.smileyplayertrader.command;

import org.bukkit.command.CommandSender;

public interface ICommand {

    void onCommand(CommandSender sender, String[] args);
    boolean isVisibleInTabComplete(CommandSender sender);

}
