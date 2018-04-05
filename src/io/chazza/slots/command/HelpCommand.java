package io.chazza.slots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.chazza.slots.Main;
import io.chazza.slots.fanciful.FancyMessage;
import io.chazza.slots.util.CenterUtil;
import io.chazza.slots.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by charliej on 14/06/2017.
 */
@CommandAlias("%command%")
public class HelpCommand extends BaseCommand {

    public HelpCommand(CommandManager cm, Plugin p){
        cm.registerCommand(this);
    }

    public void tellCommand(Player p, String cmd, String info,String permission){
        new FancyMessage(ColorUtil.translate("&8» &f/slot "+cmd))
            .tooltip(ColorUtil.translate("&8» &f/slot "+cmd), ColorUtil.translate("&7Info: &f"+info), ColorUtil.translate("&7Permission: &f"+permission))
            .suggest("/slot " + cmd)
            .send(p);
    }

    @Subcommand("help|h")
    public void onMessageCommand(Player p){
        if(p.hasPermission("sponsoredslots.help")){
            CenterUtil.sendCenteredMessage(p, "&6-&e-&6-&e-&8[ &6&lSponsored&e&lSlots &8]&e-&6-&e-&6-");
            CenterUtil.sendCenteredMessage(p, "&7Hover for more information!");
            CenterUtil.sendCenteredMessage(p, "");

            tellCommand(p, "", "Open GUI","sponsoredslots.gui");
            tellCommand(p, "reload", "Reload configuration","sponsoredslots.reload");
            tellCommand(p, "msg", "Set Slot Message","sponsoredslots.setmessage");
            tellCommand(p, "cmd", "Set Slot Command","sponsoredslots.setcommand");
        }else p.sendMessage(Main.getMessage("message.no-permission")
            .replace("%permission%", "sponsoredslots.help"));
    }
}
