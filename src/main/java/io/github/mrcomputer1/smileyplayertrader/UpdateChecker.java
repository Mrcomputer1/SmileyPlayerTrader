package io.github.mrcomputer1.smileyplayertrader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static String UPDATE_URL = "https://mrcomputer1.github.io/SmileyPlayerTrader/versions.json";
    public boolean isOutdated = false;
    public String upToDateVersion = null;
    public boolean unsupported = false;
    public boolean failed = false;

    public void checkForUpdates(){
        try {
            URL url = new URL(UPDATE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
            if(obj.has("versions")){
                JsonObject versions = obj.getAsJsonObject("versions");

                String currentMCVersion = Bukkit.getBukkitVersion().split("-")[0];
                String pluginVersion = SmileyPlayerTrader.getInstance().getDescription().getVersion();
                if(versions.has(currentMCVersion)){
                    JsonObject version = versions.get(currentMCVersion).getAsJsonObject();
                    if(version.has("unsupported")){
                        SmileyPlayerTrader.getInstance().getLogger().warning("This Minecraft version is no longer supported and therefore no support will be given for this version.");
                        Bukkit.broadcast(I18N.translate("&c[Smiley Player Trader] This Minecraft version is no longer supported and therefore no support will be given for this version."), "smileyplayertrader.admin");
                        unsupported = true;
                    }
                    if(!pluginVersion.contains("SNAPSHOT")){ // Ignore development versions
                        String latest = version.getAsJsonPrimitive("latest").getAsString();
                        if(!pluginVersion.equalsIgnoreCase(latest)){
                            SmileyPlayerTrader.getInstance().getLogger().warning("Outdated Plugin! Latest version is " + latest + ".");
                            Bukkit.broadcast(I18N.translate("&e[Smiley Player Trader] Plugin is outdated! Latest version is %0%. It is recommended to download the update.", latest), "smileyplayertrader.admin");
                            isOutdated = true;
                            upToDateVersion = latest;
                        }else{
                            SmileyPlayerTrader.getInstance().getLogger().info("Plugin is up to date.");
                        }
                    }
                }else{
                    SmileyPlayerTrader.getInstance().getLogger().warning("This version of Minecraft is not currently supported. This could be due to it being an old version or a new version that hasn't been tested yet.");
                }
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            SmileyPlayerTrader.getInstance().getLogger().warning("Failed to check plugin version!");
            Bukkit.broadcast(I18N.translate("&e[Smiley Player Trader] Failed to check plugin version!"), "smileyplayertrader.admin");
            failed = true;
        }

    }

}
