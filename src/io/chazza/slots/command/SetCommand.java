package io.chazza.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import io.chazza.slots.Main;
import io.chazza.slots.data.Slot;
import io.chazza.slots.data.SlotType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by charliej on 14/06/2017.
 */
@CommandAlias("%command%")
public class SetCommand extends BaseCommand {

    public SetCommand(CommandManager cm){
        cm.registerCommand(this);
    }

    @CommandAlias("set")
    public void onMessageCommand(CommandSender cs, @Single String slot, @Single OnlinePlayer op, @Single Integer time){
        if(cs.hasPermission("sponsoredslots.set")){

            /**
             * Check if slot exists,
             * check if slot is owned
             * if so -> queue slot
             */
            Slot s = Main.getInstance().getSlot(slot);

            if(s == null){
                cs.sendMessage(Main.getMessage("message.invalid"));
                return;
            }

            if(s.getType() == SlotType.PLACEHOLDER) return;

            if(Main.getInstance().getSlot(op.getPlayer()) == null) {

                Calendar cl = Calendar.getInstance();
                cl.setTimeInMillis(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time));
                s.setExpire(cl.getTimeInMillis());
                s.setOwner(op.getPlayer().getUniqueId());

                cs.sendMessage(Main.getMessage("message.set")
                    .replace("%id%", s.getId()).replace("%player%", op.getPlayer().getName())
                    .replace("%expire%", cl.getTime() + ""));

                return;
            }else Main.getInstance().getLogger().info("Sorry, "+op.getPlayer().getName()+" already owns a slot.");

        }else cs.sendMessage(Main.getMessage("message.no-permission")
        .replace("%permission%", "sponsoredslots.set"));
    }
}
