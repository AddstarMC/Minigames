package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2017.
 */
public abstract class AbstractAction implements ActionInterface {

    public void debug(MinigamePlayer p, ScriptObject obj){
        if (Minigames.plugin.isDebugging()){
            Minigames.plugin.getLogger().info("Debug: Execute on Obj:" + String.valueOf(obj) + " as Action: " + String.valueOf(this) + " Player: " +String.valueOf(p));
        }
    }
    void setWinnersLosers(MinigamePlayer winner){
        if(winner.getMinigame().getType() != MinigameType.SINGLEPLAYER){
            List<MinigamePlayer> w;
            List<MinigamePlayer> l;
            if(winner.getMinigame().isTeamGame()){
                w = new ArrayList<>(winner.getTeam().getPlayers());
                l = new ArrayList<>(winner.getMinigame().getPlayers().size() - winner.getTeam().getPlayers().size());
                for(Team t : TeamsModule.getMinigameModule(winner.getMinigame()).getTeams()){
                    if(t != winner.getTeam())
                        l.addAll(t.getPlayers());
                }
            }
            else{
                w = new ArrayList<>(1);
                l = new ArrayList<>(winner.getMinigame().getPlayers().size());
                w.add(winner);
                l.addAll(winner.getMinigame().getPlayers());
                l.remove(winner);
            }
            Minigames.plugin.playerManager.endMinigame(winner.getMinigame(), w, l);
        } else{
            Minigames.plugin.playerManager.endMinigame(winner);
        }
    }

}
