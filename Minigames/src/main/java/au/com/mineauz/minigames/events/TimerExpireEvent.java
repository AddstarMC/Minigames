package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;

public class TimerExpireEvent extends AbstractMinigameEvent {


    public TimerExpireEvent(Minigame minigame) {
        super(minigame);
    }

    public MinigameState getMinigameState() {
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
