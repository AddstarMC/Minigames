package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2017.
 */
public abstract class AbstractAction extends ActionInterface {

    protected void checkScore(MinigamePlayer player){
        if(player.getMinigame().isTeamGame()){
            if(player.getTeam().getScore() < player.getMinigame().getMaxScore())return;
        }else {
            if (player.getScore() < player.getMinigame().getMaxScorePerPlayer()) return;
        }
        endGameWithWinner(player);
    }

    protected void endGameWithWinner(MinigamePlayer player){
        if(player.getMinigame().getType() != MinigameType.SINGLEPLAYER){
            List<MinigamePlayer> w;
            List<MinigamePlayer> l;
            if(player.getMinigame().isTeamGame()){
                w = new ArrayList<>(player.getTeam().getPlayers());
                l = new ArrayList<>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
                for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
                    if(t != player.getTeam())
                        l.addAll(t.getPlayers());
                }
            }
            else{
                w = new ArrayList<>(1);
                l = new ArrayList<>(player.getMinigame().getPlayers().size());
                w.add(player);
                l.addAll(player.getMinigame().getPlayers());
                l.remove(player);
            }
            Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
        } else{
            Minigames.plugin.pdata.endMinigame(player);
        }
    }
}
