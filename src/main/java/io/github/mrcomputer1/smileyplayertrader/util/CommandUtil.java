package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandUtil {

    public static boolean isNotAuthorized(CommandSender target, long id){
        if(!(target instanceof Player)){
            return false; // CONSOLE always has permission
        }
        Player sender = (Player)target;

        ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_PRODUCT_BY_ID, id);
        try {
            if (set.next()) {
                if(sender.getUniqueId().toString().equalsIgnoreCase(set.getString("merchant"))){
                    return false;
                }else{
                    return !sender.hasPermission("smileyplayertrader.others");
                }
            }else{
                sender.sendMessage(I18N.translate("&cRejecting permission due to invalid ID!"));
                return true;
            }
        }catch(SQLException e){
            SmileyPlayerTrader.getInstance().getLogger().warning("Failed to check authorization status! Rejecting, just in case...");
            e.printStackTrace();
            return true;
        }
    }

}
