package au.com.mineauz.minigames.events;

import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class JoinMinigameEvent extends AbstractMinigameEvent {

    private MinigamePlayer player = null;
    private boolean betting = false;

    public JoinMinigameEvent(MinigamePlayer player, Minigame minigame) {
        this(player, minigame, false);
    }

    public JoinMinigameEvent(MinigamePlayer player, Minigame minigame, boolean betting) {
        super(minigame);
        this.player = player;
        this.betting = betting;
    }

    public MinigamePlayer getMinigamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public boolean isBetting() {
        return betting;
    }

}
