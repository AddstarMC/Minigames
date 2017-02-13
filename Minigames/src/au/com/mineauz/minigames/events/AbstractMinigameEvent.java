package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 13/02/2017.
 */
public class AbstractMinigameEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Minigame mgm;

    public AbstractMinigameEvent(Minigame mgm) {
        this.mgm = mgm;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled=b;
    }

    public Minigame getMinigame(){
        return mgm;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
