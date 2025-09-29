package io.github.mrcomputer1.smileyplayertrader.util;

import com.google.gson.*;
import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class I18N {

    private JsonObject language;
    private static String[] languages = {"en_us", "ru_ru", "pl_pl", "vi_vn", "zh_cn"};

    public void loadLanguages(){
        String lang = SmileyPlayerTrader.getInstance().getConfiguration().getCurrentLanguage();
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
            String lang = SmileyPlayerTrader.getInstance().getConfiguration().getCurrentLanguage();
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
            if(!langFolder.mkdirs()){
                SmileyPlayerTrader.getInstance().getLogger().severe("Failed to create languages directory.");
            }
        }
        for (String lang : languages) {
            File file = new File(SmileyPlayerTrader.getInstance().getDataFolder(), "languages/" + lang + ".json");
            if(!file.exists())
                SmileyPlayerTrader.getInstance().saveResource("languages/" + lang + ".json", false);
        }
    }

    public static String translate(String str, Object... args){
        boolean isDebug = SmileyPlayerTrader.getInstance().getConfiguration().getDebugI18NAlerts();

        String s;
        if(SmileyPlayerTrader.getInstance().getI18N().language.has(str)) {
            s = SmileyPlayerTrader.getInstance().getI18N().language.get(str).getAsString();
        }else{
            SmileyPlayerTrader.getInstance().getLogger().warning("Key '" + str + "' was not found in translation file!");
            s = str;
            if(isDebug)
                s = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "! " + ChatColor.RESET + s;
        }
        for(int i = 0; i < args.length; i++){
            s = s.replace("%" + i + "%", "" + args[i]);
        }
        s = ChatColor.translateAlternateColorCodes('&', s);

        return s;
    }

    public static BaseComponent translateComponents(String str, BaseComponent... args) {
        boolean isDebug = SmileyPlayerTrader.getInstance().getConfiguration().getDebugI18NAlerts();

        String s;
        if(SmileyPlayerTrader.getInstance().getI18N().language.has(str)) {
            s = SmileyPlayerTrader.getInstance().getI18N().language.get(str).getAsString();
        }else{
            SmileyPlayerTrader.getInstance().getLogger().warning("Key '" + str + "' was not found in translation file!");
            s = str;
            if(isDebug)
                s = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "! " + ChatColor.RESET + s;
        }

        return parseTranslationStringToComponent(s, args);
    }

    public static BaseComponent parseTranslationStringToComponent(String s, BaseComponent... args) {
        TextComponent baseSection = new TextComponent();
        TextComponent section = baseSection;
        StringBuilder builder = new StringBuilder();
        char[] chrArray = s.toCharArray();
        for (int i = 0; i < chrArray.length; i++) {
            char chr = chrArray[i];

            if (chr == '%') {
                // Process replacement by reading %<argIndex>%
                int argIndex = 0;

                // Parse the arg index.
                int place = 1;
                int chrIndex = i;
                char digit;
                while (Character.isDigit(digit = chrArray[++chrIndex])) {
                    argIndex += (digit - '0') * place;
                    place *= 10;
                }

                if (digit != '%') {
                    // If a non-digit is found before ending %, insert % and process characters normally to the string.
                    builder.append('%');
                } else {
                    if (argIndex >= args.length) {
                        // If index is out of range, insert the raw replacement (%<argIndex>%) to the string.
                        builder.append('%').append(argIndex).append('%');
                    } else {
                        // If index is in range, complete the string, apply the replacement and create a new component.
                        section.setText(builder.toString());
                        section.addExtra(args[argIndex]);

                        TextComponent newSection = new TextComponent();
                        section.addExtra(newSection);
                        section = newSection;

                        builder = new StringBuilder();
                    }

                    i = chrIndex;
                }
            } else if (chr == '&' || chr == '§') {
                // Process colour/formatting code.
                chr = Character.toLowerCase(chrArray[++i]);

                // If the string is not empty, complete the string and create a new component before applying the formatting.
                if (builder.length() != 0) {
                    section.setText(builder.toString());

                    TextComponent newSection = new TextComponent();
                    section.addExtra(newSection);
                    section = newSection;

                    builder = new StringBuilder();
                }

                switch (chr) {
                    case 'l':
                        section.setBold(true);
                        break;
                    case 'n':
                        section.setUnderlined(true);
                        break;
                    case 'o':
                        section.setItalic(true);
                        break;
                    case 'k':
                        section.setObfuscated(true);
                        break;
                    case 'm':
                        section.setStrikethrough(true);
                        break;
                    case 'r':
                        section.setBold(false);
                        section.setUnderlined(false);
                        section.setItalic(false);
                        section.setObfuscated(false);
                        section.setStrikethrough(false);
                        section.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                        break;
                    case '4':
                    case 'c':
                    case '6':
                    case 'e':
                    case '2':
                    case 'a':
                    case 'b':
                    case '3':
                    case '1':
                    case '9':
                    case 'd':
                    case '5':
                    case 'f':
                    case '7':
                    case '0':
                        //noinspection DataFlowIssue
                        section.setColor(ChatColor.getByChar(chr).asBungee());
                    default:
                }
            } else {
                // Add to current string.
                builder.append(chr);
            }
        }

        // Complete the string.
        section.setText(builder.toString());

        return baseSection;
    }

}
