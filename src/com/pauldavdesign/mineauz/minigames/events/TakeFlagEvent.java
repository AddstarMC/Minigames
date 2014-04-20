package com.pauldavdesign.mineauz.minigames.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.CTFFlag;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class TakeFlagEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private CTFFlag flag = null;
	private String flagName = null;
	private Minigame minigame;
	private boolean cancelled = false;
	private boolean displayMessage = true;
	private MinigamePlayer player;
	
	public TakeFlagEvent(Minigame minigame, MinigamePlayer player, CTFFlag flag){
		this.flag = flag;
		this.minigame = minigame;
		this.player = player;
	}
	
	public TakeFlagEvent(Minigame minigame, MinigamePlayer player, String flagName){
		this.flagName = flagName;
		this.minigame = minigame;
		this.player = player;
	}
	
	public boolean isCTFFlag(){
		return flag != null;
	}
	
	public CTFFlag getFlag(){
		return flag;
	}
	
	public String getFlagName(){
		return flagName;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public boolean shouldDisplayMessage(){
		return displayMessage;
	}
	
	public void setShouldDisplayMessage(boolean arg0){
		displayMessage = arg0;
	}
	
	public MinigamePlayer getPlayer(){
		return player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

}
