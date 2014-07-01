package au.com.mineauz.minigames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class JoinMinigameEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private MinigamePlayer player = null;
	private Minigame mgm = null;
	private boolean betting = false;
	private boolean cancelled = false;
	
	public JoinMinigameEvent(MinigamePlayer player, Minigame minigame){
		this.player = player;
		mgm = minigame;
	}
	
	public JoinMinigameEvent(MinigamePlayer player, Minigame minigame, boolean betting){
		this.player = player;
		mgm = minigame;
		this.betting = betting;
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
