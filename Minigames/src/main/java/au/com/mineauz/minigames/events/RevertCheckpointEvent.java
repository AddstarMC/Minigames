package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.entity.Player;

public class RevertCheckpointEvent extends AbstractCancellableMinigameEvent {
    private final MinigamePlayer player;

    public RevertCheckpointEvent(MinigamePlayer player) {
        super(null);
        this.player = player;
    }

    public MinigamePlayer getMinigamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

}
