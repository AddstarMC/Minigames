package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created for the AddstarMC Project. Created by Narimm on 13/02/2017.
 */
public class AbstractMinigameEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Minigame mgm;
    private boolean cancelled = false;

    /**
     * Instantiates a new Abstract minigame event.
     *
     * @param game the mgm
     */
    public AbstractMinigameEvent(final Minigame game) {
        super();
        this.mgm = game;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * True if cancelled.
     *
     * @return boolean
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the cancel status of the event.
     *
     * @param b boolean
     */
    @Override
    public void setCancelled(final boolean b) {
        this.cancelled = b;
    }

    /**
     * Get minigame minigame.
     *
     * @return the minigame
     */
    public Minigame getMinigame() {
        return this.mgm;
    }

    /**
     * The event handlers.
     *
     * @return HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
