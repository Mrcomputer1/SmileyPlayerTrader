package io.github.mrcomputer1.smileyplayertrader;

import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.RegionUtil;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(e.getPlayer().hasPermission("smileyplayertrader.admin")){
            if(SmileyPlayerTrader.getInstance().getUpdateChecker() != null) {
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().unsupported) {
                    e.getPlayer().sendMessage(I18N.translate("&c[Smiley Player Trader] This Minecraft version is no longer supported and therefore no support will be given for this version."));
                }
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().failed) {
                    e.getPlayer().sendMessage(I18N.translate("&e[Smiley Player Trader] Failed to check plugin version!"));
                }
                if (SmileyPlayerTrader.getInstance().getUpdateChecker().isOutdated) {
                    e.getPlayer().sendMessage(I18N.translate("&e[Smiley Player Trader] Plugin is outdated! Latest version is %0%. It is recommended to download the update.", SmileyPlayerTrader.getInstance().getUpdateChecker().upToDateVersion));
                }
            }
        }

        if(SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageEnabled()
            && SmileyPlayerTrader.getInstance().getConfiguration().getItemStorageNotifyUncollectedEarningsEnabled()){
            try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(StatementHandler.StatementType.GET_UNCOLLECTED_EARNINGS, e.getPlayer().getUniqueId().toString())) {
                if(set.next()){
                    if(set.getInt("uncollected_earnings") > 0){
                        e.getPlayer().sendMessage(I18N.translate("&2&oYou have uncollected earnings. Type &f&o/spt collect &2&oto collect."));
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        SmileyPlayerTrader.getInstance().getPlayerConfig().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        MerchantUtil.clearProductIdCache(e.getPlayer());
        SmileyPlayerTrader.getInstance().getPlayerConfig().unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent e) {
        switch (SmileyPlayerTrader.getInstance().getConfiguration().getAllowedWorldsMode()) {
            case WHITELIST: {
                if (!SmileyPlayerTrader.getInstance().getConfiguration().getAllowedWorldsList().contains(e.getEntity().getWorld().getName()))
                    return;

                break;
            }
            case BLACKLIST: {
                if (SmileyPlayerTrader.getInstance().getConfiguration().getAllowedWorldsList().contains(e.getEntity().getWorld().getName()))
                    return;

                break;
            }
        }

        if(SmileyPlayerTrader.getInstance().getConfiguration().getAutoCombatLockEnabled()) {
            if (e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();
                if(!RegionUtil.isAllowedOverall(player))
                    return;
                SmileyPlayerTrader.getInstance().getPlayerConfig().lockPlayer(player);
            }
            if (e.getEntity() instanceof Player){
                Player player = (Player) e.getEntity();
                if(!RegionUtil.isAllowedOverall(player))
                    return;
                SmileyPlayerTrader.getInstance().getPlayerConfig().lockPlayer((Player) e.getEntity());
            }
        }
    }

}
