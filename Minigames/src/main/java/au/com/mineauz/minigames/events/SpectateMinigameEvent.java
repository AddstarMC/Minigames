package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.entity.Player;

public class SpectateMinigameEvent extends AbstractCancellableMinigameEvent {
    private final MinigamePlayer player;

    public SpectateMinigameEvent(MinigamePlayer player, Minigame minigame) {
        super(minigame);
        this.player = player;
    }

    public MinigamePlayer getMinigamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }
}
