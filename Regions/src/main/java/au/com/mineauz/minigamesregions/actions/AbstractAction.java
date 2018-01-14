package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    protected ScriptObject createScriptObject(MinigamePlayer player,ScriptObject object){
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("player", "area", "minigame", "team");
            }

            @Override
            public String getAsString() {
                return "";
            }

            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return player;
                } else if (name.equalsIgnoreCase("area")) {
                    return object;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return player.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return player.getTeam();
                }

                return null;
            }
        };
        return base;
    }
}
