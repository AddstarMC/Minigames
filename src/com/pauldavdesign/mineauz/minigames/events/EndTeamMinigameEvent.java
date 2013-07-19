package com.pauldavdesign.mineauz.minigames.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class EndTeamMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private List<MinigamePlayer> losingPlayers = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> winningPlayers = new ArrayList<MinigamePlayer>();
	private Minigame minigame;
	private int winningTeam;
	private boolean cancelled = false;
	
	public EndTeamMinigameEvent(List<MinigamePlayer> losers, List<MinigamePlayer> winners, Minigame mgm, int winteam){
		losingPlayers.addAll(losers);
		winningPlayers.addAll(winners);
		minigame = mgm;
		winningTeam = winteam;
	}
	
	public List<MinigamePlayer> getLosingPlayers(){
		return losingPlayers;
	}
	
	public void setLosingPlayers(List<MinigamePlayer> losers){
		losingPlayers = losers;
	}
	
	public List<MinigamePlayer> getWinnningPlayers(){
		return winningPlayers;
	}
	
	public void setWinningPlayers(List<MinigamePlayer> winners){
		winningPlayers = winners;
	}
	
	public int getWinningTeamInt(){
		return winningTeam;
	}
	
	public void setWinningTeamInt(int winner){
		winningTeam = winner;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public boolean isCancelled(){
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
