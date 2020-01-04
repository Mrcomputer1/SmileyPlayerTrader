package io.github.mrcomputer1.smileyplayertrader.util;

import com.google.gson.*;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import org.bukkit.ChatColor;

import java.io.*;

public class I18N {

    private JsonObject language;
    private static String[] languages = {"en_us"};

    public void loadLanguages(){
        String lang = SmileyPlayerTrader.getInstance().getConfig().getString("currentLanguage", "en_us");
        File langFolder = new File(SmileyPlayerTrader.getInstance().getDataFolder(), "languages");
        JsonParser parser = new JsonParser();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(langFolder, lang + ".json")));
            language = parser.parse(isr).getAsJsonObject();
        } catch (FileNotFoundException | JsonParseException e) {
            SmileyPlayerTrader.getInstance().getLogger().warning("Invalid language. Loading en_us...");
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(langFolder, lang + ".json")));
                language = parser.parse(isr).getAsJsonObject();
            } catch (FileNotFoundException | JsonParseException ex) {
                SmileyPlayerTrader.getInstance().getLogger().warning("Failed to load default en_us. Resetting language...");
                SmileyPlayerTrader.getInstance().saveResource("languages/en_us.json", true);
                try {
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(langFolder, lang + ".json")));
                    language = parser.parse(isr).getAsJsonObject();
                } catch (FileNotFoundException | JsonParseException exc) {
                    exc.printStackTrace();
                    SmileyPlayerTrader.getInstance().getLogger().severe("Failed to load language!");
                }
            }
        }
    }

    public void updateLanguage(){
        try {
            String lang = SmileyPlayerTrader.getInstance().getConfig().getString("currentLanguage", "en_us");
            InputStream resource = SmileyPlayerTrader.getInstance().getResource("languages/" + lang + ".json");
            if (resource == null) {
                return;
            }
            JsonObject obj = new JsonParser().parse(new InputStreamReader(resource)).getAsJsonObject();
            if (obj.get("$$version$$").getAsInt() != language.get("$$version$$").getAsInt()) {
                SmileyPlayerTrader.getInstance().saveResource("languages/" + lang + ".json", true);
                this.loadLanguages();
            }
        }catch(NullPointerException e){
        }
    }

    public void createLanguages(){
        File langFolder = new File(SmileyPlayerTrader.getInstance().getDataFolder(), "languages");
        if(!langFolder.exists()) {
            langFolder.mkdirs();
            for (String lang : languages) {
                SmileyPlayerTrader.getInstance().saveResource("languages/" + lang + ".json", false);
            }
        }
    }

    public static String translate(String str, Object... args){
        String s;
        if(SmileyPlayerTrader.getInstance().getI18N().language.has(str)) {
            s = SmileyPlayerTrader.getInstance().getI18N().language.get(str).getAsString();
        }else{
            SmileyPlayerTrader.getInstance().getLogger().warning("Key '" + str + "' was not found in translation file!");
            s = str;
        }
        for(int i = 0; i < args.length; i++){
            s = s.replace("%" + i + "%", "" + args[i]);
        }
        s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }

}
