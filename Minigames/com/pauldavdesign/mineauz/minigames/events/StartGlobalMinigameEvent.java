package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class StartGlobalMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Minigame mgm;
	private final String mechanic;
	
	public StartGlobalMinigameEvent(Minigame mgm){
		this.mgm = mgm;
		mechanic = mgm.getMechanicName();
	}
	
	public Minigame getMinigame(){
		return mgm;
	}
	
	public String getMechanic(){
		return mechanic;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
