package io.github.mrcomputer1.smileyplayertrader.command;

import io.github.mrcomputer1.smileyplayertrader.SmileyPlayerTrader;
import io.github.mrcomputer1.smileyplayertrader.util.I18N;
import io.github.mrcomputer1.smileyplayertrader.util.ItemUtil;
import io.github.mrcomputer1.smileyplayertrader.util.MerchantUtil;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Commands {

    public static void add(Player sender, String username){
        OfflinePlayer target = sender;
        if(username != null){
            target = Bukkit.getOfflinePlayer(username);
            if(!sender.hasPermission("smileyplayertrader.others")){
                sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
                return;
            }
        }

        SmileyPlayerTrader.getInstance().getDatabase().run("INSERT INTO products (merchant, product, cost1, cost2, enabled) VALUES (?, ?, ?, ?, ?)",
                target.getUniqueId().toString(), null, null, null, false);

        sender.sendMessage(I18N.translate("&aAdded product %0%. Use &f/spt setcost <id> &eand &f/spt setproduct <id> &ewhile holding items, both must be set before you can &f/spt enable <id> &ethis product!", SmileyPlayerTrader.getInstance().getDatabase().getInsertId()));
    }

    public static void list(Player sender, String username){
        OfflinePlayer target = sender;
        if(username != null){
            target = Bukkit.getOfflinePlayer(username);
            if(!sender.hasPermission("smileyplayertrader.others")){
                sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
                return;
            }
        }

        ResultSet set = SmileyPlayerTrader.getInstance().getDatabase().get("SELECT * FROM products WHERE merchant=?",
                target.getUniqueId().toString());

        try {
            while (set.next()) {
                boolean isStocked = true;
                byte[] productb = set.getBytes("product");
                String products = I18N.translate(", Product: UNSET");
                if(productb != null){
                    ItemStack product = MerchantUtil.buildItem(productb);
                    if(target.isOnline() && !ItemUtil.doesPlayerHaveItem(target.getPlayer(), product)){
                        isStocked = false;
                    }
                    if(product.getItemMeta().hasDisplayName()) {
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), product.getItemMeta().getDisplayName());
                    }else{
                        products = I18N.translate(", Product: %0%x %1%", product.getAmount(), product.getType());
                    }
                }

                byte[] cost1b = set.getBytes("cost1");
                String cost1s = I18N.translate(", Cost 1: UNSET");
                if(cost1b != null){
                    ItemStack cost1 = MerchantUtil.buildItem(cost1b);
                    if(cost1.getItemMeta().hasDisplayName()) {
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), cost1.getItemMeta().getDisplayName());
                    }else{
                        cost1s = I18N.translate(", Cost 1: %0%x %1%", cost1.getAmount(), cost1.getType());
                    }
                }

                byte[] cost2b = set.getBytes("cost2");
                String cost2s = I18N.translate(", Cost 2: UNSET");
                if(cost2b != null) {
                    ItemStack cost2 = MerchantUtil.buildItem(cost2b);
                    if(cost2.getItemMeta().hasDisplayName()) {
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), cost2.getItemMeta().getDisplayName());
                    }else{
                        cost2s = I18N.translate(", Cost 2: %0%x %1%", cost2.getAmount(), cost2.getType());
                    }
                }
                if(set.getBoolean("enabled")) {
                    if (isStocked) {
                        sender.sendMessage(I18N.translate("&e - %0% %1% %2% %3%, Enabled: YES", set.getLong("id"), products, cost1s, cost2s));
                    } else {
                        sender.sendMessage(I18N.translate("&c - [OUT OF STOCK] %0% %1% %2% %3%, Enabled: YES", set.getLong("id"), products, cost1s, cost2s));
                    }
                }else{
                    sender.sendMessage(I18N.translate("&4 - %0% %1% %2% %3%, Enabled: NO", set.getLong("id"), products, cost1s, cost2s));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void remove(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        SmileyPlayerTrader.getInstance().getDatabase().run("DELETE FROM products WHERE id=?", id);
        sender.sendMessage(I18N.translate("&2Deleted product!"));
    }

    public static void setCost(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        CraftItemStack.asNMSCopy(sender.getInventory().getItemInMainHand()).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET cost1=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aCost set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void setCost(Player sender, long id, Material material){
        setCost(sender, id, material, 1);
    }

    public static void setCost(Player sender, long id, Material material, int count){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        ItemStack is = new ItemStack(material);
        is.setAmount(count);
        CraftItemStack.asNMSCopy(is).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET cost1=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aCost set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void setCost2(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        CraftItemStack.asNMSCopy(sender.getInventory().getItemInMainHand()).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET cost2=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aSecondary cost set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void setCost2(Player sender, long id, Material material){
        setCost2(sender, id, material, 1);
    }

    public static void setCost2(Player sender, long id, Material material, int count){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        ItemStack is = new ItemStack(material);
        is.setAmount(count);
        CraftItemStack.asNMSCopy(is).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET cost2=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aSecondary cost set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void setResult(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        CraftItemStack.asNMSCopy(sender.getInventory().getItemInMainHand()).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET product=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aProduct set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void setResult(Player sender, long id, Material material){
        setResult(sender, id, material, 1);
    }

    public static void setResult(Player sender, long id, Material material, int count){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        NBTTagCompound ntc = new NBTTagCompound();
        ItemStack is = new ItemStack(material);
        is.setAmount(count);
        CraftItemStack.asNMSCopy(is).save(ntc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(ntc, baos);

            SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET product=? WHERE id=?", baos.toByteArray(), id);

            sender.sendMessage(I18N.translate("&aProduct set!"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void enable(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET enabled=1 WHERE id=?", id);

        sender.sendMessage(I18N.translate("&aEnabled product!"));
    }

    public static void disable(Player sender, long id){
        if(isNotAuthorized(sender, id)){
            sender.sendMessage(I18N.translate("&cWhoops! You are not authorized to edit others products!"));
            return;
        }

        SmileyPlayerTrader.getInstance().getDatabase().run("UPDATE products SET enabled=0 WHERE id=?", id);

        sender.sendMessage(I18N.translate("&aDisabled product!"));
    }

    private static boolean isNotAuthorized(Player sender, long id){
        ResultSet set = SmileyPlayerTrader.getInstance().getDatabase().get("SELECT * FROM products WHERE id=?", id);
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
