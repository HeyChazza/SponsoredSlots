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
public class MessageCommand extends BaseCommand implements Listener {

    public List<UUID> setMessage;

    public MessageCommand(CommandManager cm, Plugin p){
        cm.registerCommand(this);
        Bukkit.getPluginManager().registerEvents(this, p);

        setMessage = new ArrayList<>();
    }

    @Subcommand("message|msg")
    public void onMessageCommand(Player p){
        if(p.hasPermission("sponsoredslots.setmessage")){
            Slot s = Main.getInstance().getSlot(p);
            if(s != null){
                setMessage.add(p.getUniqueId());
                p.sendMessage(Main.getMessage("message.msg-in-chat"));
            }else{
                p.sendMessage(Main.getMessage("message.not-own"));
            }
        }else p.sendMessage(Main.getMessage("message.no-permission")
            .replace("%permission%", "sponsoredslots.setmessage"));
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        Slot s = Main.getInstance().getSlot(p);

        if(e.getMessage().toLowerCase().equals("exit")){
            e.setCancelled(true);
            e.setCancelled(true);
            setMessage.remove(p.getUniqueId());
        }


        if(setMessage.contains(p.getUniqueId())){
            p.sendMessage(Main.getMessage("message.message")
                .replace("%message%", e.getMessage()));
            s.setMessage(e.getMessage());
            e.setCancelled(true);
            setMessage.remove(p.getUniqueId());
        }
    }

}
