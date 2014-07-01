package au.com.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class StartGlobalMinigameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Minigame mgm;
	private final String mechanic;
	private final MinigamePlayer caller;
	
	public StartGlobalMinigameEvent(Minigame mgm, MinigamePlayer caller){
		this.mgm = mgm;
		mechanic = mgm.getMechanicName();
		this.caller = caller;
	}
	
	public Minigame getMinigame(){
		return mgm;
	}
	
	public String getMechanic(){
		return mechanic;
	}
	
	public MinigamePlayer getCaller(){
		return caller;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
