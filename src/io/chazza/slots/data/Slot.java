package io.chazza.slots.data;

import io.chazza.slots.Main;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Created by charliej on 14/06/2017.
 */
public class Slot {

    private String slotId, slotMessage, slotCommand;
    private Enum<SlotType> slotType;
    private ItemStack unclaimedItem;
    private ItemStack claimedItem;
    private List<Integer> slotNumbers;
    private boolean unclaimedGlow;
    private boolean claimedGlow;
    private double slotCost;
    private Integer slotPeriod;
    private UUID slotOwner;
    private Long slotExpire;

    public Slot(String slotId){
        this.slotId = slotId;
    }

    public String getId(){
        return slotId;
    }

    public Slot withType(Enum<SlotType> slotType){
        this.slotType = slotType;
        return this;
    }
    public Slot withUnclaimedItem(ItemStack unclaimedItem){
        this.unclaimedItem = unclaimedItem;
        return this;
    }
    public Slot withClaimedItem(ItemStack claimedItem){
        this.claimedItem = claimedItem;
        return this;
    }
    public Slot withSlotNumbers(List<Integer> slotNumbers){
        this.slotNumbers = slotNumbers;
        return this;
    }
    public Slot withUnclaimedGlow(Boolean unclaimedGlow){
        this.unclaimedGlow = unclaimedGlow;
        return this;
    }
    public Slot withClaimedGlow(Boolean claimedGlow){
        this.claimedGlow = claimedGlow;
        return this;
    }
    public Slot withCost(Double slotCost){
        this.slotCost = slotCost;
        return this;
    }
    public Slot withPeriod(Integer slotPeriod){
        this.slotPeriod = slotPeriod;
        return this;
    }
    public Slot withExpire(Long slotExpire){
        this.slotExpire = slotExpire;
        return this;
    }
    public Slot withOwner(UUID slotOwner){
        this.slotOwner = slotOwner;
        return this;
    }
    public Slot withCommand(String slotCommand){
        this.slotCommand = slotCommand;
        return this;
    }

    public void build(){
        Main.getInstance().getSlots().add(this);
    }

    public Enum<SlotType> getType(){
        return slotType;
    }
    public ItemStack getUnclaimedItem(){
        return unclaimedItem;
    }
    public ItemStack getClaimedItem(){
        if(slotType == SlotType.PLACEHOLDER) return unclaimedItem;
        return claimedItem;
    }
    public List<Integer> getSlotNumbers(){
        return slotNumbers;
    }
    public boolean hasUnclaimedGlow(){
        return unclaimedGlow;
    }
    public boolean hasClaimedGlow(){
        if(slotType == SlotType.PLACEHOLDER) return unclaimedGlow;
        return claimedGlow;
    }
    public double getPrice(){
        return slotCost;
    }
    public Integer getPeriod(){
        return slotPeriod;
    }
    public UUID getOwner(){
        return slotOwner;
    }
    public void setOwner(UUID slotOwner){
        this.slotOwner = slotOwner;
        if(slotOwner != null)
            Main.getInstance().getConfig().set("slot."+slotId+".setting.owner", slotOwner.toString());
        else
            Main.getInstance().getConfig().set("slot."+slotId+".setting.owner", slotOwner);

            Main.getInstance().saveConfig();
    }
    public Long getExpire(){
        return slotExpire;
    }
    public void setExpire(Long slotExpire){
        this.slotExpire = slotExpire;
        Main.getInstance().getConfig().set("slot."+slotId+".setting.expire", slotExpire);
        Main.getInstance().saveConfig();
    }
    public String getMessage(){
        return slotMessage != null ? ChatColor.stripColor(slotMessage) : Main.getMessage("message.default-msg");
    }
    public void setMessage(String slotMessage){
        Main.getInstance().getConfig().set("slot."+slotId+".setting.message", slotMessage);
        Main.getInstance().saveConfig();

        this.slotMessage = Main.getInstance().getConfig().getString("slot."+slotId+".setting.message");
    }
    public String getCommand(){
        return slotCommand != null ? ChatColor.stripColor(slotCommand) : Main.getMessage("message.default-cmd");
    }
    public void setCommand(String slotCommand){
        Main.getInstance().getConfig().set("slot."+slotId+".setting.command", slotCommand);
        Main.getInstance().saveConfig();

        this.slotCommand = Main.getInstance().getConfig().getString("slot."+slotId+".setting.command");
    }

}
