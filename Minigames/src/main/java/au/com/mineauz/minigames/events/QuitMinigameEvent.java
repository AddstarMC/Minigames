package au.com.mineauz.minigames.events;

import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class QuitMinigameEvent extends AbstractMinigameEvent {
    private MinigamePlayer player = null;
    private boolean isForced = false;
    private boolean isWinner = false;

    public QuitMinigameEvent(MinigamePlayer player, Minigame minigame, boolean forced, boolean isWinner) {
        super(minigame);
        this.player = player;
        isForced = forced;
        this.isWinner = isWinner;
    }

    public MinigamePlayer getMinigamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }


    public boolean isForced() {
        return isForced;
    }

    public boolean isWinner() {
        return isWinner;
    }

}
