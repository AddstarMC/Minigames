package au.com.mineauz.minigames.events;

import java.util.List;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class EndMinigameEvent extends AbstractMinigameEvent {

    private List<MinigamePlayer> winners = null;
    private List<MinigamePlayer> losers = null;


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


