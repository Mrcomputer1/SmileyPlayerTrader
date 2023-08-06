package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.gui.framework.bedrock.BedrockGUI;
import io.github.mrcomputer1.smileyplayertrader.util.impl.bedrock.IBedrockImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.lang.reflect.InvocationTargetException;
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

    private static IBedrockImpl bedrockImpl;

    private static boolean bindBedrockImplIfPossible(){
        if(!isCumulusAvailable())
            return false;

        if(GeyserUtil.bedrockImpl != null)
            return true;

        try {
            Class<?> clazz;
            if (isFloodgate()) {
                clazz = Class.forName("io.github.mrcomputer1.smileyplayertrader.util.impl.bedrock.FloodgateImpl");
            }else if(isGeyser()){
                clazz = Class.forName("io.github.mrcomputer1.smileyplayertrader.util.impl.bedrock.GeyserImpl");
            }else{
                return false;
            }

            GeyserUtil.bedrockImpl = (IBedrockImpl) clazz.getConstructor().newInstance();
            return true;
        }catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            return false;
        }
    }

    public static boolean isBedrockPlayer(Player player){
        if(!bindBedrockImplIfPossible())
            return false;

        return GeyserUtil.bedrockImpl.isBedrockPlayer(player);
    }

    public static void showSimpleForm(Player player, String title, String content){
        if(!bindBedrockImplIfPossible())
            return;

        GeyserUtil.bedrockImpl.showSimpleForm(player, title, content);
    }

    public static void showConfirmationForm(Player player, String title, String content, Consumer<Boolean> callback){
        if(!bindBedrockImplIfPossible())
            return;

        GeyserUtil.bedrockImpl.showConfirmationForm(player, title, content, callback);
    }

    public static void showFormDelayed(Player player, BedrockGUI gui){
        if(!bindBedrockImplIfPossible())
            return;

        GeyserUtil.bedrockImpl.showFormDelayed(player, gui);
    }

    public static void showForm(Player player, BedrockGUI gui){
        if(!bindBedrockImplIfPossible())
            return;

        GeyserUtil.bedrockImpl.showForm(player, gui);
    }

}
