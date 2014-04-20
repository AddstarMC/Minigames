package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigameTimer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MinigameTimerTickEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private Minigame minigame;
	private MinigameTimer timer;
	
	public MinigameTimerTickEvent(Minigame minigame, MinigameTimer timer){
		this.minigame = minigame;
		this.timer = timer;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public int getTimeLeft(){
		return timer.getTimeLeft();
	}
	
	public void setTimeLeft(int time){
		timer.setTimeLeft(time);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
