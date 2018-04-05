package io.chazza.slots;

import io.chazza.slots.data.Slot;
import io.chazza.slots.data.SlotType;
import io.chazza.slots.util.ColorUtil;
import io.chazza.slots.util.Glow;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by charliej on 14/06/2017.
 */
public class Main extends JavaPlugin {

    private static Main slotsInstance;
    public static Main getInstance(){
        return slotsInstance;
    }
    public static Economy economy;

    public List<Slot> slots;
    public List<Slot> getSlots(){
        return slots;
    }


    public List<Slot> getOwnedSlots(){
        List<Slot> ownedSlots = new ArrayList<>();

        for(Slot s : getSlots()){
            if(s.getType() == SlotType.SLOT && s.getOwner() != null) ownedSlots.add(s);
        }
        return ownedSlots;
    }

    public Slot getSlot(int slotId){
        for(Slot s : getSlots()){
            if(s.getSlotNumbers().contains(slotId)){
                return s;
            }
        }

        return null;
    }
    public Slot getSlot(Player p){
        for(Slot s : slots){
            if(s.getOwner() != null && s.getOwner().equals(p.getUniqueId())) return s;
        }
        return null;
    }
    public Slot getSlot(String slotId){
        for(Slot s : slots){
            if(s.getId().equalsIgnoreCase(slotId)) return s;
        }
        return null;
    }

    public static String getMessage(String path){
        path = "general."+path;

        String prefix = getInstance().getConfig().get("general.message.prefix") != null ?
            ColorUtil.translate(getInstance().getConfig().getString("general.message.prefix"))
            : "";

        return getInstance().getConfig().get(path) != null ?
            ColorUtil.translate(getInstance().getConfig().getString(path)).replace("%PREFIX%", prefix)
            : "§6[§e§lSlot§6] §7That message could not be found.";
    }

    public static void handleReload(){
        Setup.slots(getInstance());
        Setup.commands(getInstance());
        Setup.announcer(getInstance());
        getInstance().reloadConfig();
    }

    public void onEnable(){
        slotsInstance = this;
        saveDefaultConfig();

        Setup.slots(this);
        Setup.commands(this);
        Setup.announcer(this);
        Setup.checker(this);

        Glow.getGlow();

        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Setup.economy(this);
        }else{
            getLogger().log(Level.WARNING, "-          -          -          -          -          -");
            getLogger().log(Level.WARNING, "Vault is not installed.");
            getLogger().log(Level.WARNING, "This is required for SponsoredSlots to function.");
            getLogger().log(Level.WARNING, "The plugin will now be disabled.");
            getLogger().log(Level.WARNING, "-          -          -          -          -          -");
        }
    }

    public void onDisable(){
        slotsInstance = null;
    }
}
