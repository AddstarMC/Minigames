package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.jetbrains.annotations.Nullable;

public abstract class ScoreAction extends AbstractAction {

    void checkScore(@Nullable MinigamePlayer player) {
        if (player == null || !player.isInMinigame()) return;
        if (player.getScore() >= player.getMinigame().getMaxScorePerPlayer() || (player.getMinigame().isTeamGame() && player.getTeam().getScore() >= player.getMinigame().getMaxScore())) {
            setWinnersLosers(player);
        }
    }

}
