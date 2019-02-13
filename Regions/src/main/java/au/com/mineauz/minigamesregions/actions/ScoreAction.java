package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;

/**
 * au.com.mineauz.minigamesregions.actions
 * Created for the Addstar MC for Minigames-Project
 * Created by Narimm on 2/03/2018.
 */
public abstract class ScoreAction extends AbstractAction{

    void checkScore(MinigamePlayer player){
        if(player == null || !player.isInMinigame())return;
        if( player.getScore()  >= player.getMinigame().getMaxScorePerPlayer()|| (player.getMinigame().isTeamGame() && player.getTeam().getScore() >= player.getMinigame().getMaxScore())){
            setWinnersLosers(player);
        }
    }

}
