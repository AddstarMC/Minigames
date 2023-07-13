package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;

import java.util.List;

public class EndMinigameEvent extends AbstractMinigameEvent {
    private final List<MinigamePlayer> winners;
    private final List<MinigamePlayer> losers;


    public EndMinigameEvent(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame minigame) {
        super(minigame);
        this.winners = winners;
        this.losers = losers;
    }

    public List<MinigamePlayer> getWinners() {
        return winners;
    }

    public List<MinigamePlayer> getLosers() {
        return losers;
    }
}


