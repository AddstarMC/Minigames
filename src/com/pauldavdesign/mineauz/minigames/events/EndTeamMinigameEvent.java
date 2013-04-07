package com.pauldavdesign.mineauz.minigames.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class EndTeamMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private List<Player> losingPlayers = new ArrayList<Player>();
	private List<Player> winningPlayers = new ArrayList<Player>();
	private Minigame minigame;
	private int winningTeam;
	private boolean cancelled = false;
	
	public EndTeamMinigameEvent(List<Player> losers, List<Player> winners, Minigame mgm, int winteam){
		losingPlayers.addAll(losers);
		winningPlayers.addAll(winners);
		minigame = mgm;
		winningTeam = winteam;
	}
	
	public List<Player> getLosingPlayers(){
		return losingPlayers;
	}
	
	public void setLosingPlayers(List<Player> losers){
		losingPlayers = losers;
	}
	
	public List<Player> getWinnningPlayers(){
		return winningPlayers;
	}
	
	public void setWinningPlayers(List<Player> winners){
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
