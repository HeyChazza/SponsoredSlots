package io.chazza.slots.event.custom;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;
import java.util.UUID;

/**
 * Created by charliej on 31/01/2017.
 */
public class SSPurchaseEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private String id;
    private UUID uuid;
    private Date purchaseDate;
    private Date expireDate;

    public SSPurchaseEvent(UUID uuid, String id, Date purchaseDate, Date expireDate){
        this.uuid = uuid;
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
    }

    public UUID getPlayer(){
        return uuid;
    }

    public String getID(){
        return id;
    }

    public Date getPurchase(){
        return purchaseDate;
    }

    public Date getExpire(){
        return expireDate;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
