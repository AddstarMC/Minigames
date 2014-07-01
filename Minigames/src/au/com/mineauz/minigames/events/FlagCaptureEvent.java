package au.com.mineauz.minigames.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.CTFFlag;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class FlagCaptureEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private MinigamePlayer player;
	private Minigame minigame;
	private CTFFlag flag;
	private boolean displayMessage = true;
	
	public FlagCaptureEvent(Minigame minigame, MinigamePlayer player, CTFFlag flag){
		this.player = player;
		this.flag = flag;
		this.minigame = minigame;
	}
	
	public MinigamePlayer getPlayer() {
		return player;
	}

	public Minigame getMinigame() {
		return minigame;
	}

	public CTFFlag getFlag() {
		return flag;
	}
	
	public boolean shouldDisplayMessage(){
		return displayMessage;
	}
	
	public void setShouldDisplayMessage(boolean arg0){
		displayMessage = arg0;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}

}
