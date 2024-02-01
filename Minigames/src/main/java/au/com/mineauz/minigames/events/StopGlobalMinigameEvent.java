package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public class StopGlobalMinigameEvent extends AbstractCancellableMinigameEvent {
    private final @NotNull String mechanic;
    private final Audience caller;

    public StopGlobalMinigameEvent(@NotNull Minigame mgm, Audience caller) {
        super(mgm);
        mechanic = mgm.getMechanicName();
        this.caller = caller;
    }

    public @NotNull String getMechanicName() {
        return mechanic;
    }

    public Audience getCaller() {
        return caller;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        throw new UnsupportedOperationException("Cannot cancel a  Minigames Broadcast Event");
    }
}
