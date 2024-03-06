package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;

public class DropFlagEvent extends AbstractCancellableMinigameEvent {
    private final CTFFlag flag;
    private final MinigamePlayer player;
    private boolean displayMessage = true;

    public DropFlagEvent(Minigame mgm, CTFFlag flag, MinigamePlayer player) {
        super(mgm);
        this.flag = flag;
        this.player = player;
    }

    public MinigamePlayer getPlayer() {
        return player;
    }

    public CTFFlag getFlag() {
        return flag;
    }

    public boolean shouldDisplayMessage() {
        return displayMessage;
    }

    public void setShouldDisplayMessage(boolean arg0) {
        displayMessage = arg0;
    }
}
