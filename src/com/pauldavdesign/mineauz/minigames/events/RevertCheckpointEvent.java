package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RevertCheckpointEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private Player player = null;
	private boolean cancelled = false;
	
	public RevertCheckpointEvent(Player player){
		this.player = player;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public boolean isCancelled(){
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
