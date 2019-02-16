package au.com.mineauz.minigames.events;

import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;

public class RevertCheckpointEvent extends AbstractMinigameEvent {

    private MinigamePlayer player = null;

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
