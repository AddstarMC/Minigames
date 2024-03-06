package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;

public class FlagCaptureEvent extends AbstractMinigameEvent {

    private final MinigamePlayer player;
    private final CTFFlag flag;
    private boolean displayMessage = true;

    public FlagCaptureEvent(Minigame minigame, MinigamePlayer player, CTFFlag flag) {
        super(minigame);
        this.player = player;
        this.flag = flag;
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
