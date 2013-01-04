package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameTimer;

public class TimerExpireEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private MinigameTimer timer;
	private Minigame minigame;
	
	public TimerExpireEvent(MinigameTimer timer, Minigame minigame){
		this.timer = timer;
		this.minigame = minigame;
	}
	
	public MinigameTimer getMinigameTimer(){
		return timer;
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
