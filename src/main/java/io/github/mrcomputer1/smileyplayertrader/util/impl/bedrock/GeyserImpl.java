package io.github.mrcomputer1.smileyplayertrader.util.impl.bedrock;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockGUI;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;
import java.util.function.Consumer;

public class GeyserImpl implements IBedrockImpl{

    public boolean isBedrockPlayer(Player player){
        UUID uuid = player.getUniqueId();

        return GeyserApi.api().isBedrockPlayer(uuid);
    }

    public void showSimpleForm(Player player, String title, String content){
        SimpleForm form = SimpleForm.builder()
                .title(title)
                .content(content)
                .button(I18N.translate("OK"))
                .build();

        UUID uuid = player.getUniqueId();

        GeyserApi.api().sendForm(uuid, form);
    }

    public void showConfirmationForm(Player player, String title, String content, Consumer<Boolean> callback){
        Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () -> {
            ModalForm form = ModalForm.builder()
                    .title(title)
                    .content(content)
                    .button1(I18N.translate("Yes"))
                    .button2(I18N.translate("No"))
                    .closedOrInvalidResultHandler(() -> callback.accept(false))
                    .validResultHandler(result -> callback.accept(result.clickedFirst()))
                    .build();

            UUID uuid = player.getUniqueId();

            GeyserApi.api().sendForm(uuid, form);
        }, 20L);
    }

    public void showFormDelayed(Player player, BedrockGUI gui){
        Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () -> showForm(player, gui), 20L);
    }

    public void showForm(Player player, BedrockGUI gui){
        Form form = (Form) gui.buildForm();
        UUID uuid = player.getUniqueId();

        GeyserApi.api().sendForm(uuid, form);
    }

}
