package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerConfig {

    public static class Config implements Cloneable{
        private boolean sqlConnected = true;

        public boolean tradeToggle = true;
        public boolean combatNoticeToggle = true;

        public Config clone() {
            try {
                return (Config)super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private int LOCK_PERIOD = SmileyPlayerTrader.getInstance().getConfiguration().getAutoCombatLockLength() * 1000;
    private Map<String, Config> playerConfigs = new HashMap<>();
    private Map<String, Long> playerCombatLock = new HashMap<>();
    private Map<UUID, Long> playerTradeCooldown = new HashMap<>();

    public void loadPlayer(Player player){
        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.LOAD_PLAYER_CONFIG, player.getUniqueId().toString())) {
            if(set.next()){
                Config c = new Config();
                c.tradeToggle = set.getBoolean("trade_toggle");
                c.combatNoticeToggle = set.getBoolean("combat_notice_toggle");
                this.playerConfigs.put(player.getName(), c);
            }else{
                SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.CREATE_DEFAULT_PLAYER_CONFIG, player.getUniqueId().toString());
                Config c = new Config();
                this.playerConfigs.put(player.getName(), c);
            }
        } catch (SQLException ex) {
            SmileyPlayerTrader.getInstance().getLogger().severe("Failed to load settings of " + player.getName() + " (" + player.getUniqueId().toString() + "). See below stack trace for further information.");
            player.sendMessage(I18N.translate("&c[Smiley Player Trader] We were unable to load your settings. This error has been logged to the server console for server administrator to review."));
            ex.printStackTrace();

            Config c = new Config();
            c.sqlConnected = false;
            this.playerConfigs.put(player.getName(), c);
        }
    }

    public void reloadPlayers(){
        this.playerConfigs.clear(); // ensure empty
        for(Player p : Bukkit.getOnlinePlayers()){
            loadPlayer(p);
        }
    }

    public Config getPlayer(Player player){
        return this.playerConfigs.get(player.getName());
    }

    public Config getMutablePlayer(Player player){
        return this.playerConfigs.get(player.getName()).clone();
    }

    public void unloadPlayer(Player player){
        this.playerConfigs.remove(player.getName());
        this.playerCombatLock.remove(player.getName());
        this.playerTradeCooldown.remove(player.getUniqueId());
    }

    public void updatePlayer(Player player, Config config){
        if(!this.playerConfigs.get(player.getName()).sqlConnected){
            this.playerConfigs.put(player.getName(), config);
        }else{
            SmileyPlayerTrader.getInstance().getStatementHandler().run(StatementHandler.StatementType.UPDATE_PLAYER_CONFIG, config.tradeToggle, config.combatNoticeToggle, player.getUniqueId().toString());
            this.playerConfigs.put(player.getName(), config);
        }
    }

    public void lockPlayer(Player player){
        Config playerConfig = getPlayer(player);
        if(!SmileyPlayerTrader.getInstance().getConfiguration().getAutoCombatLockNeverShowNotice()
                && !isLocked(player) && playerConfig != null && playerConfig.combatNoticeToggle){
            player.sendMessage(I18N.translate("&7Player trading has been temporarily disabled while you are in combat. Use &f/spt releasecombatlock &7to re-enable trading early."));

            Config c = getMutablePlayer(player);
            c.combatNoticeToggle = false;
            updatePlayer(player, c);
        }
        this.playerCombatLock.put(player.getName(), System.currentTimeMillis());
    }

    public boolean isLocked(Player player){
        if(!this.playerCombatLock.containsKey(player.getName()))
            return false;

        long lockTime = this.playerCombatLock.get(player.getName());

        if((lockTime + LOCK_PERIOD) < System.currentTimeMillis()){
            releasePlayerLock(player);
            return false;
        }else return true;
    }

    public void releasePlayerLock(Player player){
        this.playerCombatLock.remove(player.getName());
    }

    public boolean isTradeOnCooldown(Player player) {
        if (!SmileyPlayerTrader.getInstance().getConfiguration().getCooldownEnabled())
            return false;
        if (player.hasPermission("smileyplayertrader.bypasscooldown"))
            return false;
        if (!playerTradeCooldown.containsKey(player.getUniqueId()))
            return false;
        return playerTradeCooldown.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    public int getCooldownTimeRemaining(Player player) {
        if (!playerTradeCooldown.containsKey(player.getUniqueId()))
            return -1;
        return (int) ((playerTradeCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000);
    }

    public void setTradeCooldown(Player player) {
        if (!SmileyPlayerTrader.getInstance().getConfiguration().getCooldownEnabled())
            return;
        if (player.hasPermission("smileyplayertrader.bypasscooldown"))
            return;
        playerTradeCooldown.put(player.getUniqueId(),
                System.currentTimeMillis() + (SmileyPlayerTrader.getInstance().getConfiguration().getCooldownLength() * 1000L));
    }

}
