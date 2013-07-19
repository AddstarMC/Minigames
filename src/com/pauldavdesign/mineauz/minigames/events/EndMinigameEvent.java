package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class EndMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private MinigamePlayer player = null;
	private Minigame mgm = null;
	private boolean cancelled = false;
	
	public EndMinigameEvent(MinigamePlayer player, Minigame minigame){
		this.player = player;
		mgm = minigame;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public MinigamePlayer getPlayer() {
		return player;
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
