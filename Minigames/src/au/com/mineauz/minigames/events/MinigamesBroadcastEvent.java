package au.com.mineauz.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.minigame.Minigame;

public class MinigamesBroadcastEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private String message;
	private String prefix;
	private Minigame minigame;
	
	public MinigamesBroadcastEvent(String prefix, String message, Minigame minigame){
		this.message = message;
		this.minigame = minigame;
		this.prefix = prefix;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getMessageWithPrefix(){
		return prefix + " " + message;
	}
	
	public void setMessage(String message){
		this.message = message;
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
