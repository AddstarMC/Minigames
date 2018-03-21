package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptObject;

import java.util.ArrayList;
import java.util.List;

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
