package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MinigamePlayer;

public class TakeFlagEvent extends AbstractCancellableMinigameEvent {
    private final CTFFlag flag;
    private final String flagName;
    private final MinigamePlayer player;
    private boolean displayMessage = true;

    public TakeFlagEvent(Minigame minigame, MinigamePlayer player, CTFFlag flag) {
        this(minigame, player, flag, null);
    }

    public TakeFlagEvent(Minigame minigame, MinigamePlayer player, String flagName) {
        this(minigame, player, null, flagName);
    }

    public TakeFlagEvent(Minigame minigame, MinigamePlayer player, CTFFlag flag, String flagName) {
        super(minigame);
        this.flag = flag;
        this.flagName = flagName;
        this.player = player;
    }


    public boolean isCTFFlag() {
        return flag != null;
    }

    public CTFFlag getFlag() {
        return flag;
    }

    public String getFlagName() {
        return flagName;
    }

    public boolean shouldDisplayMessage() {
        return displayMessage;
    }

    public void setShouldDisplayMessage(boolean arg0) {
        displayMessage = arg0;
    }

    public MinigamePlayer getPlayer() {
        return player;
    }

}
