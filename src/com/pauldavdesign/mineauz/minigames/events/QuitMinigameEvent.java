package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class QuitMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private MinigamePlayer player = null;
	private Minigame mgm = null;
	private boolean cancelled = false;
	private boolean isForced = false;
	
	public QuitMinigameEvent(MinigamePlayer player, Minigame minigame, boolean forced){
		this.player = player;
		mgm = minigame;
		setForced(forced);
	}
	
	public MinigamePlayer getMinigamePlayer(){
		return player;
	}
	
	public Player getPlayer(){
		return player.getPlayer();
	}
	
	public Minigame getMinigame(){
		return mgm;
	}
	
    public boolean isForced() {
		return isForced;
	}

	public void setForced(boolean isForced) {
		this.isForced = isForced;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
