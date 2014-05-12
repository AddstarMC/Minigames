package com.pauldavdesign.mineauz.minigames.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class StartMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private boolean willTeleport;
	private List<MinigamePlayer> players;
	private Minigame minigame;
	
	public StartMinigameEvent(List<MinigamePlayer> players, Minigame minigame, boolean willTeleport){
		this.willTeleport = willTeleport;
		this.players = players;
		this.minigame = minigame;
	}
	
	public boolean getWillTeleport(){
		return willTeleport;
	}
	
	public List<MinigamePlayer> getPlayers(){
		return new ArrayList<MinigamePlayer>(players);
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
