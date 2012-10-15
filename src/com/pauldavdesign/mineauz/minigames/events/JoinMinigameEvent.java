package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class JoinMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private Player player = null;
	private Minigame mgm = null;
	private boolean betting = false;
	private boolean cancelled = false;
	
	public JoinMinigameEvent(Player player, Minigame minigame){
		this.player = player;
		mgm = minigame;
	}
	
	public JoinMinigameEvent(Player player, Minigame minigame, boolean betting){
		this.player = player;
		mgm = minigame;
		this.betting = betting;
	}
	
    public Player getPlayer() {
		return player;
	}

	public Minigame getMinigame() {
		return mgm;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isBetting(){
		return betting;
	}

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
