package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class RevertCheckpointEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private MinigamePlayer player = null;
	private boolean cancelled = false;
	
	public RevertCheckpointEvent(MinigamePlayer player){
		this.player = player;
	}
	
	public MinigamePlayer getPlayer(){
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
