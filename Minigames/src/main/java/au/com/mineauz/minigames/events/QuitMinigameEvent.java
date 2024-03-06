package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.entity.Player;

public class QuitMinigameEvent extends AbstractMinigameEvent {
    private final MinigamePlayer player;
    private final boolean isForced;
    private final boolean isWinner;

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
