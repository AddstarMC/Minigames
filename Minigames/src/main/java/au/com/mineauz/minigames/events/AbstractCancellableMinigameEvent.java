package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.event.Cancellable;

public class AbstractCancellableMinigameEvent extends AbstractMinigameEvent implements Cancellable {
    private boolean cancelled = false;

    /**
     * Instantiates a new Abstract minigame event.
     *
     * @param game the mgm
     */
    public AbstractCancellableMinigameEvent(final Minigame game) {
        super(game);
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
}