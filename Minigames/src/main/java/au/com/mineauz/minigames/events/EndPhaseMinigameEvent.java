package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;

import java.util.List;

/**
 * This event is called whenever a minigame enters it's endphase.
 * This happens amongst others if a team wins or a finished sign was used.
 * However, this is NOT a guaranteed indicator that a minigame is about to end.
 * While the Endphase will lead to the end of a minigame, this event will NOT be called if certain circumstances happen,
 * like if the /mg quit command was called.
 * <p>
 * if you need an event for cleanup use {@link EndedMinigameEvent}
 */
public class EndPhaseMinigameEvent extends AbstractCancellableMinigameEvent {
    private final List<MinigamePlayer> winners;
    private final List<MinigamePlayer> losers;


    public EndPhaseMinigameEvent(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame minigame) {
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


