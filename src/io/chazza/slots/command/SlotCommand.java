package io.chazza.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.CommandAlias;
import io.chazza.slots.Main;
import io.chazza.slots.Setup;
import io.chazza.slots.data.Slot;
import io.chazza.slots.data.SlotType;
import io.chazza.slots.event.custom.SSPurchaseEvent;
import io.chazza.slots.util.ColorUtil;
import io.chazza.slots.util.Glow;
import io.chazza.slots.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by charliej on 14/06/2017.
 */
public class SlotCommand extends BaseCommand implements Listener {

    public SlotCommand(CommandManager cm, Plugin p){
        cm.registerCommand(this);
        Bukkit.getPluginManager().registerEvents(this, p);
    }

    public Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, Main.getInstance().getConfig().getInt("general.gui.size"),
            ColorUtil.translate(Main.getInstance().getConfig().getString("general.gui.title")));

        Setup.slots(Main.getInstance());
        for(Slot s : Main.getInstance().getSlots()){
            if(s.getOwner() != null){
                for(int slotNumber : s.getSlotNumbers()){
                    ItemStack is = s.getClaimedItem();
                    if(is.hasItemMeta()) {
                        ItemMeta im = is.getItemMeta();


                        List<String> lre = new ArrayList<>();
                        if(im.hasLore()) {

                            Long diff = s.getExpire() - System.currentTimeMillis();

                            for (String l : im.getLore()) {
                                lre.add(l.replace("%price%", s.getPrice() + "")
                                    .replace("%period%", TimeUtil.toString(diff))
                                    .replace("%message%", getSlotMsg(s.getId()))
                                    .replace("%command%", s.getCommand())
                                    .replace("%player%", Bukkit.getOfflinePlayer(s.getOwner()).getName()));
                            }
                            im.setDisplayName(im.getDisplayName().replace("%player%", Bukkit.getOfflinePlayer(s.getOwner()).getName()));
                            im.setLore(lre);

                            is.setItemMeta(im);


                            if(is.getType() == Material.SKULL_ITEM && is.getDurability() == 3){
                                SkullMeta  meta = (SkullMeta) is.getItemMeta();
                                meta.setOwner(Bukkit.getOfflinePlayer(s.getOwner()).getName());
                                is.setItemMeta(meta);

                            }


                            if(s.hasClaimedGlow()){
                                Glow.addGlow(is);
                            }

                        }
                    }
                    inv.setItem(slotNumber, is);
                }
            }else {
                for(int slotNumber : s.getSlotNumbers()){
                    ItemStack is = s.getUnclaimedItem();
                    if(is.hasItemMeta()) {
                        ItemMeta im = is.getItemMeta();
                        if(im.hasLore()) {

                            Slot ownedSlot = Main.getInstance().getSlot(p);
                            String ownedId = ownedSlot != null ? ownedSlot.getId() : "N/A";
                            Long expireTime = Main.getInstance().getConfig().getLong("slot."+ownedId+".setting.expire");
                            String expire = expireTime != 0 ?
                                TimeUtil.toString(expireTime - System.currentTimeMillis()) : "N/A";

                            List<String> lre = new ArrayList<>();
                            for (String l : im.getLore()) {
                                lre.add(l.replace("%price%", s.getPrice() + "")
                                    .replace("%period%", TimeUtil.toString(TimeUnit.SECONDS.toMillis(s.getPeriod())))
                                    .replace("%owned%", ownedId)
                                    .replace("%expired%", expire));
                            }
                            im.setLore(lre);

                            is.setItemMeta(im);

                            if(s.hasUnclaimedGlow()){
                                Glow.addGlow(is);
                            }
                        }
                    }
                    inv.setItem(slotNumber, is);
                }
            }
        }
        return inv;
    }

    public String getSlotMsg(String slotId){
        String msg = Main.getInstance().getConfig().getString("slot."+slotId+".setting.message");
        return msg != null ? ChatColor.stripColor(msg) : Main.getMessage("message.default-msg");
    }

    @CommandAlias("%command%")
    public void onSlotCommand(Player p){
        p.openInventory(getInventory(p));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e){
        HumanEntity he = e.getWhoClicked();

        if (he instanceof Player) {
            Player p = (Player) he;
            Inventory inv = e.getInventory();

            if(inv.getHolder() != null) return;

            if(inv.getName().equalsIgnoreCase(getInventory(p).getName()) && inv.getSize() == getInventory(p).getSize()){
                e.setCancelled(true);

                if(Main.getInstance().getSlot(e.getSlot()) != null){
                    Slot s = Main.getInstance().getSlot(e.getSlot());
                    if(s.getType() == SlotType.SLOT){

                        if(s.getOwner() != null){
                            p.sendMessage(Main.getMessage("message.teleported")
                                .replace("%player%", Bukkit.getOfflinePlayer(s.getOwner()).getName()));

                            p.performCommand(s.getCommand().replace("%player%", Bukkit.getOfflinePlayer(s.getOwner()).getName()));
                        }else{

                            if(Main.getInstance().getSlot(p) != null){
                                Slot ownedSlot = Main.getInstance().getSlot(p);
                                Long diff = ownedSlot.getExpire() - System.currentTimeMillis();
                                p.sendMessage(Main.getMessage("message.already-own")
                                    .replace("%expire%", TimeUtil.toString(diff)));
                                return;
                            }

                            if(Main.economy.has(p, s.getPrice())) {
                                s.setOwner(p.getUniqueId());

                                Calendar cl = Calendar.getInstance();
                                cl.setTimeInMillis(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(s.getPeriod()));
                                s.setExpire(cl.getTimeInMillis());
                                Main.economy.withdrawPlayer(p, s.getPrice());

                                Long diff = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(s.getPeriod())) - System.currentTimeMillis();

                                SSPurchaseEvent purchaseEvent = new SSPurchaseEvent(p.getUniqueId(), s.getId(), new Date(System.currentTimeMillis()), new Date(cl.getTimeInMillis()));
                                Bukkit.getPluginManager().callEvent(purchaseEvent);

                                p.sendMessage(Main.getMessage("message.purchased")
                                    .replace("%price%", s.getPrice() + "")
                                    .replace("%expire%", TimeUtil.toString(diff)));

                                p.closeInventory();
                                Setup.slots(Main.getInstance());
                                p.openInventory(getInventory(p));
                            }else{
                                p.sendMessage(Main.getMessage("message.not-enough"));
                                p.closeInventory();
                            }
                        }
                    }else{
                        String cmd = Main.getInstance().getConfig().getString("slot."+s.getId()+".display.setting.command");
                        if(cmd == null) return;
                        if(cmd.isEmpty()) return;

                        p.closeInventory();
                        if(cmd.startsWith("player: ")) {
                            p.performCommand(cmd.replace("%player%", p.getName())
                                .replace("player: ", ""));

                        }else if(cmd.startsWith("console: ")){
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                cmd.replace("%player%", p.getName())
                            .replace("console: ", ""));
                        }
                    }
                }
            }
        }
    }
}
