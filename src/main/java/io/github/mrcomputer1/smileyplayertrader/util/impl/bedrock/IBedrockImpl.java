package io.github.mrcomputer1.smileyplayertrader.util.impl.bedrock;

import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockGUI;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface IBedrockImpl {

    boolean isBedrockPlayer(Player player);
    void showSimpleForm(Player player, String title, String content);
    void showConfirmationForm(Player player, String title, String content, Consumer<Boolean> callback);
    void showFormDelayed(Player player, BedrockGUI gui);
    void showForm(Player player, BedrockGUI gui);

}
