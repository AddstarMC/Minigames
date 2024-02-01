package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import org.jetbrains.annotations.NotNull;

public class TimerExpireEvent extends AbstractCancellableMinigameEvent {

    public TimerExpireEvent(@NotNull Minigame minigame) {
        super(minigame);
    }

    public @NotNull MinigameState getMinigameState() {
        return getMinigame().getState();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        throw new UnsupportedOperationException("Cannot cancel a  Minigames TimerExpire Event");
    }
}
