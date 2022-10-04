package io.github.mrcomputer1.smileyplayertrader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BugWarner {

    private static final String BUGS_URL = "https://mrcomputer1.github.io/SmileyPlayerTrader/bugs.json";
    public int foundBugs = 0;

    public boolean checkForBugs() {
        List<String> bugsFound = new ArrayList<>();

        try {
            URL url = new URL(BUGS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();

            JsonObject versions = obj.getAsJsonObject("versions");
            JsonObject bugs = obj.getAsJsonObject("bugs");

            String pluginVersion = SmileyPlayerTrader.getInstance().getDescription().getVersion();
            if (versions.has(pluginVersion)) {
                versions.getAsJsonObject(pluginVersion).getAsJsonArray("bugs").forEach(e -> {
                    bugsFound.add(e.getAsString());
                });
            }

            boolean disable = false;
            this.foundBugs = bugsFound.size();
            for (String bug : bugsFound) {
                if (bugs.has(bug)) {
                    JsonObject bugObj = bugs.getAsJsonObject(bug);
                    SmileyPlayerTrader.getInstance().getLogger().severe("Bug Found: " +
                            bugObj.get("notes").getAsString());

                    if (bugObj.has("link"))
                        SmileyPlayerTrader.getInstance().getLogger().severe("More information can be found at: " +
                                bugObj.get("link").getAsString());

                    if (bugObj.has("fixedVersion"))
                        SmileyPlayerTrader.getInstance().getLogger().severe("This bug has been fixed in version " +
                                bugObj.get("fixedVersion").getAsString());

                    if (bugObj.has("disable")) {
                        if (bugObj.get("disable").getAsBoolean()) {
                            disable = true;
                        }
                    }
                }
            }

            if (SmileyPlayerTrader.getInstance().getConfiguration().getCheckForBugsShouldDisable() && disable) {
                SmileyPlayerTrader.getInstance().getLogger().severe("Disabling due to bugs...");
                Bukkit.getPluginManager().disablePlugin(SmileyPlayerTrader.getInstance());
                return true;
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}