package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
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
    /**
     * Logs Debug re these 2 items.
     *
     * @param p the player
     * @param obj a script object
     */
    public void debug(final MinigamePlayer p, final ScriptObject obj) {
        if (Minigames.getPlugin().isDebugging()) {
            Minigames.getPlugin().getLogger().info("Debug: Execute on Obj:"
                    + obj.getAsString() + " as Action: " + this + " Player: "
                    + p.getAsString());
        }
    }
    /**
     * Set winners losers.
     *
     * @param winner the winner
     */
    void setWinnersLosers(final MinigamePlayer winner) {
        if (winner.getMinigame().getType() != MinigameType.SINGLEPLAYER) {
            final List<MinigamePlayer> w;
            final List<MinigamePlayer> l;
            if (winner.getMinigame().isTeamGame()) {
                w = new ArrayList<>(winner.getTeam().getPlayers());
                l = new ArrayList<>(winner.getMinigame().getPlayers().size()
                        - winner.getTeam().getPlayers().size());
                for (final Team t
                        :TeamsModule.getMinigameModule(winner.getMinigame()).getTeams()) {
                    if (t != winner.getTeam()) {
                        l.addAll(t.getPlayers());
                    }
                }
            } else {
                w = new ArrayList<>(1);
                l = new ArrayList<>(winner.getMinigame().getPlayers().size());
                w.add(winner);
                l.addAll(winner.getMinigame().getPlayers());
                l.remove(winner);
            }
            Minigames.getPlugin().getPlayerManager().endMinigame(winner.getMinigame(), w, l);
        } else {
            Minigames.getPlugin().getPlayerManager().endMinigame(winner);
        }
    }
}
