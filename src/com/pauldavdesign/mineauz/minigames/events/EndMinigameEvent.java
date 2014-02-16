package com.pauldavdesign.mineauz.minigames.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class EndMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private List<MinigamePlayer> winners = null;
	private List<MinigamePlayer> losers = null;
	private Minigame mgm = null;
	private boolean cancelled = false;
	
	public EndMinigameEvent(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame minigame){
		this.winners = winners;
		this.losers = losers;
		mgm = minigame;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public List<MinigamePlayer> getWinners(){
		return winners;
	}
	
	public List<MinigamePlayer> getLosers(){
		return losers;
	}

	public Minigame getMinigame() {
		return mgm;
	}

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
