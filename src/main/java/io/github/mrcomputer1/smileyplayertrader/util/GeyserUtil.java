package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;
import java.util.function.Consumer;

public class GeyserUtil {

    private static final String GEYSER_PLUGIN = "Geyser-Spigot";
    private static final String FLOODGATE_PLUGIN = "floodgate";

    private static boolean isFloodgate(){
        return Bukkit.getPluginManager().getPlugin(FLOODGATE_PLUGIN) != null;
    }

    private static boolean isGeyser(){
        return Bukkit.getPluginManager().getPlugin(GEYSER_PLUGIN) != null;
    }

    private static boolean isCumulusAvailable(){
        return isFloodgate() || isGeyser();
    }

    public static boolean isBedrockPlayer(Player player){
        UUID uuid = player.getUniqueId();

        if(isFloodgate()){
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }else if(isGeyser()){
            return GeyserApi.api().isBedrockPlayer(uuid);
        }else{
            return false;
        }
    }

    public static void showSimpleForm(Player player, String title, String content){
        if(!isCumulusAvailable())
            return;

        SimpleForm form = SimpleForm.builder()
                .title(title)
                .content(content)
                .button(I18N.translate("OK"))
                .build();

        UUID uuid = player.getUniqueId();

        if(isFloodgate()){
            FloodgateApi.getInstance().sendForm(uuid, form);
        }else if(isGeyser()){
            GeyserApi.api().sendForm(uuid, form);
        }
    }

    public static void showConfirmationForm(Player player, String title, String content, Consumer<Boolean> callback){
        if(!isCumulusAvailable())
            return;

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

            if (isFloodgate()) {
                FloodgateApi.getInstance().sendForm(uuid, form);
            } else if (isGeyser()) {
                GeyserApi.api().sendForm(uuid, form);
            }
        }, 20L);
    }

    public static void showFormDelayed(Player player, BedrockGUI gui){
        Bukkit.getScheduler().scheduleSyncDelayedTask(SmileyPlayerTrader.getInstance(), () -> showForm(player, gui), 20L);
    }

    public static void showForm(Player player, BedrockGUI gui){
        if(!isCumulusAvailable())
            return;

        Form form = (Form) gui.buildForm();
        UUID uuid = player.getUniqueId();

        if(isFloodgate()){
            FloodgateApi.getInstance().sendForm(uuid, form);
        }else if(isGeyser()){
            GeyserApi.api().sendForm(uuid, form);
        }
    }

}
