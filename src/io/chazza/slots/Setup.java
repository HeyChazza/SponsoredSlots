package io.chazza.slots;

import co.aikar.commands.ACF;
import co.aikar.commands.CommandManager;
import io.chazza.slots.command.*;
import io.chazza.slots.data.Slot;
import io.chazza.slots.data.SlotType;
import io.chazza.slots.util.ColorUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by charliej on 14/06/2017.
 */
public class Setup {

    public static void commands(Plugin p){
        CommandManager cm = ACF.createManager(p);
        String cmd = "sponsoredslots|slots|slot|sslots";
        cm.getCommandReplacements().addReplacement("%command%", cmd);
        new SlotCommand(cm, p);
        new SetCommand(cm);
        new MessageCommand(cm, p);
        new ReloadCommand(cm, p);
        new HelpCommand(cm, p);
        new SetCmdCommand(cm, p);

    }

    public static void economy(Plugin p){
        RegisteredServiceProvider<Economy> economyProvider = p.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Main.economy = economyProvider.getProvider();
        }
    }

    public static void slots(Plugin p){

        Main.getInstance().slots = new ArrayList<>();

        SlotType slotType;
        ItemStack unclaimedItem;
        ItemMeta unclaimedMeta;
        ItemStack claimedItem;
        ItemMeta claimedMeta;

        boolean unclaimedGlow;
        boolean claimedGlow;

        int slotPeriod;
        double slotPrice;
        long slotExpire = 0;

        List<Integer> slotNumbers;
        UUID slotOwner;

        for(String slotId : p.getConfig().getConfigurationSection("slot").getKeys(false)){
            slotOwner = null;
            slotType = SlotType.valueOf(p.getConfig().getString("slot."+slotId+".type"));
            slotPeriod = p.getConfig().getInt("slot."+slotId+".setting.period");
            slotPrice = p.getConfig().getDouble("slot."+slotId+".setting.price");

            slotNumbers = p.getConfig().getIntegerList("slot."+slotId+".slot");

            if(slotType == null) {
                p.getLogger().log(Level.WARNING, "Slot Type for " + slotId + " is not defined. Skipping..");
                continue;
            }

            if(slotType == SlotType.SLOT){
                unclaimedItem = new ItemStack(Material.valueOf(p.getConfig().getString("slot."+slotId+".display.unclaimed.item")));
                unclaimedMeta = unclaimedItem.getItemMeta();
                unclaimedMeta.setDisplayName(ColorUtil.translate(p.getConfig().getString("slot."+slotId+".display.unclaimed.name")));
                unclaimedMeta.setLore(ColorUtil.translate(p.getConfig().getStringList("slot."+slotId+".display.unclaimed.lore")));
                unclaimedItem.setDurability(Short.valueOf(p.getConfig().getString("slot."+slotId+".display.unclaimed.data")));
                unclaimedItem.setItemMeta(unclaimedMeta);

                unclaimedGlow = p.getConfig().getBoolean("slot."+slotId+".display.unclaimed.setting.glow");


                claimedItem = new ItemStack(Material.valueOf(p.getConfig().getString("slot."+slotId+".display.claimed.item")));
                claimedMeta = claimedItem.getItemMeta();
                claimedMeta.setDisplayName(ColorUtil.translate(p.getConfig().getString("slot."+slotId+".display.claimed.name")));
                claimedMeta.setLore(ColorUtil.translate(p.getConfig().getStringList("slot."+slotId+".display.claimed.lore")));
                claimedItem.setDurability(Short.valueOf(p.getConfig().getString("slot."+slotId+".display.claimed.data")));
                claimedItem.setItemMeta(claimedMeta);

                claimedGlow = p.getConfig().getBoolean("slot."+slotId+".display.claimed.setting.glow");

                slotOwner = p.getConfig().getString("slot."+slotId+".setting.owner") != null
                    ? UUID.fromString(p.getConfig().getString("slot."+slotId+".setting.owner")) : null;


                slotExpire = p.getConfig().getLong("slot."+slotId+".setting.expire");

            }else{
                unclaimedItem = new ItemStack(Material.valueOf(p.getConfig().getString("slot."+slotId+".display.item")));
                unclaimedMeta = unclaimedItem.getItemMeta();
                unclaimedMeta.setDisplayName(ColorUtil.translate(p.getConfig().getString("slot."+slotId+".display.name")));
                unclaimedMeta.setLore(ColorUtil.translate(p.getConfig().getStringList("slot."+slotId+".display.lore")));
                unclaimedItem.setDurability(Short.valueOf(p.getConfig().getString("slot."+slotId+".display.data")));
                unclaimedItem.setItemMeta(unclaimedMeta);


                unclaimedGlow = p.getConfig().getBoolean("slot."+slotId+".display.setting.glow");
                claimedGlow = unclaimedGlow;

                claimedItem = unclaimedItem;
            }


            String cnfCmd = Main.getInstance().getConfig().getString("slot."+slotId+".setting.command");
            String cmd = cnfCmd != null ? ChatColor.stripColor(cnfCmd) : Main.getMessage("message.default-cmd");


            Slot s = new Slot(slotId)
                .withType(slotType)
                .withClaimedItem(claimedItem)
                .withUnclaimedItem(unclaimedItem)
                .withClaimedGlow(claimedGlow)
                .withUnclaimedGlow(unclaimedGlow)
                .withSlotNumbers(slotNumbers)
                .withCost(slotPrice)
                .withPeriod(slotPeriod)
                .withExpire(slotExpire)
                .withCommand(cmd)
                .withOwner(slotOwner);

            s.build();
        }
    }


    public static void announcer(Plugin p){
        int delay = p.getConfig().get("general.announcer.delay") != null ? p.getConfig().getInt("general.announcer.delay") : 100;

        new BukkitRunnable(){
            int current;
            @Override
            public void run() {
                if(Main.getInstance().getOwnedSlots().size() == 0) return;
                if ((++current) >= Main.getInstance().getOwnedSlots().size()) {
                    current = 0;
                }

                if (current < Main.getInstance().getOwnedSlots().size()) {
                    Slot s = Main.getInstance().getOwnedSlots().get(current);
                    String msg = Main.getInstance().getConfig().getString("slot."+s.getId()+".setting.message");

                    if(msg == null || msg.equalsIgnoreCase(Main.getMessage("message.default-msg"))){
                        return;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        for(String ann : Main.getInstance().getConfig().getStringList("general.announcer.format")){
                            p.sendMessage(ColorUtil.translate(ann)
                                .replace("%message%", msg)
                                .replace("%command%", s.getCommand())
                                .replace("%player%", Bukkit.getOfflinePlayer(s.getOwner()).getName()));
                        }

                    }
                }
            }
        }.runTaskTimerAsynchronously(p, delay, delay);
    }
    public static void checker(Plugin p){
        new BukkitRunnable(){
            int current;
            @Override
            public void run() {
                if(Main.getInstance().getOwnedSlots().size() == 0) return;
                if ((++current) >= Main.getInstance().getOwnedSlots().size()) {
                    current = 0;
                }

                if (current < Main.getInstance().getOwnedSlots().size()) {
                    Slot s = Main.getInstance().getOwnedSlots().get(current);
                    Long expire = Main.getInstance().getConfig().getLong("slot." + s.getId() + ".setting.expire");


                    if (Main.getInstance().getConfig().getString("slot." + s.getId() + ".setting.owner") != null) {
                        if ((expire - System.currentTimeMillis()) <= 0) {

                            Player p = Bukkit.getPlayer(s.getOwner());

                            if (Bukkit.getPlayer(s.getOwner()) != null) {
                                p.sendMessage(Main.getMessage("message.expired"));
                            }

                            Main.getInstance().getSlot(s.getId()).setOwner(null);
                            Main.getInstance().getSlot(s.getId()).setMessage(null);
                            Main.getInstance().getSlot(s.getId()).setExpire(null);
                            return;

                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(p, 20, 20);
    }
}
