package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.minigame.Minigame;

public class MinigameTimerTickEvent extends AbstractCancellableMinigameEvent {

    private final MinigameTimer timer;

    public MinigameTimerTickEvent(Minigame minigame, MinigameTimer timer) {
        super(minigame);
        this.timer = timer;
    }

    public long getTimeLeft() {
        return timer.getTimeLeft();
    }

    public void setTimeLeft(int time) {
        timer.setTimeLeft(time);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        throw new UnsupportedOperationException("Cannot cancel a  Minigames tick Event");
    }

}
