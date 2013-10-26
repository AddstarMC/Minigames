package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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

	public MinigamePlayer getMinigamePlayer() {
		return player;
	}
	
	public Player getPlayer(){
		return player.getPlayer();
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
