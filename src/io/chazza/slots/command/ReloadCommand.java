package io.chazza.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.chazza.slots.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Created by charliej on 14/06/2017.
 */
@CommandAlias("%command%")
public class ReloadCommand extends BaseCommand {

    public ReloadCommand(CommandManager cm, Plugin p){
        cm.registerCommand(this);
    }

    @Subcommand("reload|rl")
    public void onMessageCommand(CommandSender cs){
        if(cs.hasPermission("sponsoredslots.reload")){
            Main.handleReload();
            cs.sendMessage(Main.getMessage("message.reload"));
        }else cs.sendMessage(Main.getMessage("message.no-permission")
        .replace("%permission%", "sponsoredslots.reload"));
    }
}
