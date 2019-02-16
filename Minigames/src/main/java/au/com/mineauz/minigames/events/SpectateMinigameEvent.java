package au.com.mineauz.minigames.events;

import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class SpectateMinigameEvent extends AbstractMinigameEvent {
    private MinigamePlayer player = null;

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
