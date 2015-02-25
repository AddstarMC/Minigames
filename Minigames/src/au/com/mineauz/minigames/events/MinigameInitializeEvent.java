package au.com.mineauz.minigames.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;

/**
 * This event is for adding any custom initialization to a minigame.
 * It is called upon creating a minigame, changing the MinigameType, and changing the mechanic
 */
public class MinigameInitializeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Minigame minigame;
	public MinigameInitializeEvent(Minigame minigame) {
		this.minigame = minigame;
	}
	
	/**
	 * @return Returns the minigame being initialized
	 */
	public Minigame getMinigame() {
		return minigame;
	}
	
	/**
	 * Adds a module to this minigame.
	 * @param module The module to add, cannot be null
	 */
	public void addModule(Class<? extends MinigameModule> module) {
		Validate.notNull(module);
		minigame.addModule(module);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
