package io.chazza.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.chazza.slots.Main;
import io.chazza.slots.data.Slot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by charliej on 14/06/2017.
 */
@CommandAlias("%command%")
public class SetCmdCommand extends BaseCommand implements Listener {

    public List<UUID> setCommand;

    public SetCmdCommand(CommandManager cm, Plugin p){
        cm.registerCommand(this);
        Bukkit.getPluginManager().registerEvents(this, p);

        setCommand = new ArrayList<>();
    }

    public boolean isValidCommand(String msg){
        for(String s : Main.getInstance().getConfig().getStringList("command-filter")){
            if(msg.startsWith(s)) return true;
        }
        return false;
    }
    @Subcommand("command|cmd")
    public void onMessageCommand(Player p){
        if(p.hasPermission("sponsoredslots.setcommand")) {
            Slot s = Main.getInstance().getSlot(p);
            if (s != null) {
                setCommand.add(p.getUniqueId());
                p.sendMessage(Main.getMessage("message.cmd-in-chat"));
            } else {
                p.sendMessage(Main.getMessage("message.not-own"));
            }
        }else p.sendMessage(Main.getMessage("message.no-permission")
            .replace("%permission%", "sponsoredslots.setcommand"));
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        Slot s = Main.getInstance().getSlot(p);

        if(setCommand.contains(p.getUniqueId())){
            String msg = e.getMessage().replace("%player%", p.getName());

            if(msg.toLowerCase().equals("exit")){
                e.setCancelled(true);
                setCommand.remove(p.getUniqueId());
                return;
            }

            if(isValidCommand(msg)) {
                p.sendMessage(Main.getMessage("message.command-set")
                    .replace("%command%", msg));
                s.setCommand(msg);
                e.setCancelled(true);
                setCommand.remove(p.getUniqueId());
            }else{
                p.sendMessage(Main.getMessage("message.invalid-cmd"));
                e.setCancelled(true);
            }
        }
    }

}
