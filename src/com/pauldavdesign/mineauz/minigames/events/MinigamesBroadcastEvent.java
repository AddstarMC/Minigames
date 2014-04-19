package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MinigamesBroadcastEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private String message;
	private Minigame minigame;
	
	public MinigamesBroadcastEvent(String message, Minigame minigame){
		this.message = message;
		this.minigame = minigame;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
