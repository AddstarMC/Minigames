package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbstractMinigameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Minigame mgm;

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
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
