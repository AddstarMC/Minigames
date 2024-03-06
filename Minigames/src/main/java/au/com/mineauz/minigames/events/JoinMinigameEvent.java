package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.entity.Player;

public class JoinMinigameEvent extends AbstractMinigameEvent {

    private final MinigamePlayer player;
    private final boolean betting;

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
