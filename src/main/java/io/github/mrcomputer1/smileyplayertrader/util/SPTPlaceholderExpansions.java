package io.github.mrcomputer1.smileyplayertrader.util;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.database.statements.StatementHandler;
import io.github.mrcomputer1.smileyplayertrader.util.item.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.merchant.MerchantUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SPTPlaceholderExpansions extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return SmileyPlayerTrader.getInstance().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", SmileyPlayerTrader.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return SmileyPlayerTrader.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("trades_all")) {
            // All Trades
            return tradesAll(player);
        } else if(params.equalsIgnoreCase("trades_all_everyone")) {
            // All Trades - Everyone
            return tradesAll(null);
        } else if (params.equalsIgnoreCase("trades_active")) {
            // Active Trades (including out of stock)
            return tradesActive(player);
        } else if (params.equalsIgnoreCase("trades_active_everyone")) {
            // Active Trades (included out of stock) - Everyone
            return tradesActive(null);
        } else if (params.equalsIgnoreCase("trades_inactive")) {
            // Inactive Trades (Hidden & Disabled)
            return tradesInactive(player);
        } else if (params.equalsIgnoreCase("trades_inactive_everyone")) {
            // Inactive Trades (Hidden & Disabled) - Everyone
            return tradesInactive(null);
        } else if (params.equalsIgnoreCase("trades_hidden")) {
            // Hidden Trades
            return tradesHidden(player);
        } else if (params.equalsIgnoreCase("trades_hidden_everyone")) {
            // Hidden Trades - Everyone
            return tradesHidden(null);
        } else if (params.equalsIgnoreCase("trades_disabled")) {
            // Disabled Trades
            return tradesDisabled(player);
        } else if(params.equalsIgnoreCase("trades_disabled_everyone")) {
            // Disabled Trades - Everyone
            return tradesDisabled(null);
        } else if (params.equalsIgnoreCase("trades_in_stock")) {
            // In Stock Trades (hidden and disabled not included) - requires player
            return tradesInStock(player);
        } else if (params.equalsIgnoreCase("trades_out_of_stock")) {
            // Out of Stock Trades (hidden and disabled not included) - requires player
            return tradesOutOfStock(player);
        }

        return null;
    }

    private String getCount(StatementHandler.StatementType type, Object... params) {
        try (ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(type, params)) {
            set.next();
            return String.valueOf(set.getInt("count"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String tradesAll(OfflinePlayer player) {
        if(player != null) {
            return getCount(StatementHandler.StatementType.COUNT_ALL_PRODUCTS_FOR, player.getUniqueId().toString());
        } else {
            return getCount(StatementHandler.StatementType.COUNT_ALL_PRODUCTS_GLOBAL);
        }
    }

    private String tradesActive(OfflinePlayer player) {
        if(player != null) {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_FOR, player.getUniqueId().toString(), 1, 1);
        } else {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_GLOBAL, 1, 1);
        }
    }

    private String tradesInactive(OfflinePlayer player) {
        if(player != null) {
            return getCount(StatementHandler.StatementType.COUNT_INACTIVE_PRODUCTS_FOR, player.getUniqueId().toString());
        } else {
            return getCount(StatementHandler.StatementType.COUNT_INACTIVE_PRODUCTS_GLOBAL);
        }
    }

    private String tradesHidden(OfflinePlayer player) {
        if(player != null) {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_FOR, player.getUniqueId().toString(), 0, 0);
        } else {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_GLOBAL, 0, 0);
        }
    }

    private String tradesDisabled(OfflinePlayer player) {
        if(player != null) {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_FOR, player.getUniqueId().toString(), 1, 0);
        } else {
            return getCount(StatementHandler.StatementType.COUNT_PRODUCTS_GLOBAL, 1, 0);
        }
    }

    private String tradesInStock(OfflinePlayer player) {
        if(player == null)
            return "Error: No player.";

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.FIND_PRODUCTS, player.getUniqueId().toString())) {
            int count = 0;
            while(set.next()) {
                if(!set.getBoolean("enabled") || !set.getBoolean("available"))
                    continue;

                int purchaseLimit = set.getInt("purchase_limit");
                int purchaseCount = set.getInt("purchase_count");
                if(purchaseLimit != -1 && purchaseCount >= purchaseLimit)
                    continue;

                byte[] productb = set.getBytes("product");
                ItemStack is;
                if(productb != null) {
                    is = MerchantUtil.buildItem(productb);
                    if (is == null) {
                        continue;
                    }
                }else{
                    continue;
                }

                if(ItemUtil.doesPlayerHaveItem(player, is, set.getLong("id")))
                    count++;
            }

            return String.valueOf(count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String tradesOutOfStock(OfflinePlayer player) {
        if(player == null)
            return "Error: No player.";

        try(ResultSet set = SmileyPlayerTrader.getInstance().getStatementHandler().get(
                StatementHandler.StatementType.FIND_PRODUCTS, player.getUniqueId().toString())) {
            int count = 0;
            while(set.next()) {
                if(!set.getBoolean("enabled") || !set.getBoolean("available"))
                    continue;

                int purchaseLimit = set.getInt("purchase_limit");
                int purchaseCount = set.getInt("purchase_count");
                if(purchaseLimit != -1 && purchaseCount >= purchaseLimit)
                    continue;

                byte[] productb = set.getBytes("product");
                ItemStack is;
                if(productb != null) {
                    is = MerchantUtil.buildItem(productb);
                    if (is == null) {
                        continue;
                    }
                }else{
                    continue;
                }

                if(!ItemUtil.doesPlayerHaveItem(player, is, set.getLong("id")))
                    count++;
            }

            return String.valueOf(count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
