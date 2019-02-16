package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class GameMechanicBase implements Listener {
    public static Minigames plugin;
    public MinigamePlayerManager pdata;
    public MinigameManager mdata;

    public GameMechanicBase() {
        plugin = Minigames.getPlugin();
        pdata = plugin.getPlayerManager();
        mdata = plugin.getMinigameManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Gets the mechanics name.
     *
     * @return The name of the Mechanic
     */
    public abstract String getMechanic();

    /**
     * Gives the valid types for this game mechanic
     *
     * @return All valid game types.
     */
    public abstract EnumSet<MinigameType> validTypes();

    /**
     * Checks if a mechanic is allowed to start with the current settings. Caller
     * can be sent message, but can also be null, in which case, should be sent
     * to the console.
     *
     * @param minigame The Minigame in which settings to check
     * @param caller   The Player (or Null) to send the error messages to
     * @return true if all checks pass.
     */
    public abstract boolean checkCanStart(Minigame minigame, MinigamePlayer caller);

    /**
     * In the case of a Minigame having teams, this should be used to balance players
     * to a specific team, usual games is evenly distributed, in the case of Infection,
     * only a specific percentage is assigned to one team by default. The default function
     * will assign teams automatically unless overridden.
     * Additionally teams that are flagged as autoBalance false will not have players removed or added through a team switch...
     *
     * @param players  The players to be balanced to a team
     * @param minigame The minigame in which the balancing occours
     * @return List of {@link MinigamePlayer} that have been moved to a different or new team.
     */

    public List<MinigamePlayer> balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
        List<MinigamePlayer> result = new ArrayList<>();
        if (minigame.isTeamGame()) {
            boolean sorted = false;
            for (MinigamePlayer ply : players) {
                if (ply.getTeam() == null) {
                    Team smt = null;
                    for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                        if (smt == null || (t.getPlayers().size() < smt.getPlayers().size() &&
                                (t.getMaxPlayers() == 0 || t.getPlayers().size() != t.getMaxPlayers())))
                            smt = t;
                    }
                    if (smt == null) {
                        pdata.quitMinigame(ply, false);
                        ply.sendMessage(MinigameUtils.getLang("minigame.full"), MinigameMessageType.ERROR);
                    } else {
                        smt.addPlayer(ply);
                        ply.sendInfoMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()));
                        mdata.sendMinigameMessage(minigame,
                                String.format(smt.getGameAutobalanceMessage(),
                                        ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
                    }
                }
            }

            while (!sorted) {
                Team smt = null;
                Team lgt = null;
                for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                    if (smt == null || (t.getPlayers().size() < smt.getPlayers().size() - 1 && !t.isFull() && t.getAutoBalanceTeam()))
                        smt = t;
                    if ((lgt == null || (t.getPlayers().size() > lgt.getPlayers().size() && !t.isFull())) && t != smt && t.getAutoBalanceTeam())
                        lgt = t;
                }
                if (smt != null && lgt != null && lgt.getPlayers().size() - smt.getPlayers().size() > 1) {
                    MinigamePlayer pl = lgt.getPlayers().get(0);
                    MultiplayerType.switchTeam(minigame, pl, smt);
                    result.add(pl);
                    pl.sendInfoMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()));
                    mdata.sendMinigameMessage(minigame,
                            String.format(smt.getGameAutobalanceMessage(),
                                    pl.getDisplayName(minigame.usePlayerDisplayNames()), smt.getChatColor() + smt.getDisplayName()), null, pl);
                } else {
                    sorted = true;
                }
            }
        }
        return result;
    }

    void autoBalanceonDeath(MinigamePlayer ply, Minigame mgm) {
        Team smt = null;
        Team lgt = ply.getTeam();
        if (lgt.getAutoBalanceTeam()) {//this team is flagged as  balanced - players will be removed.
            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                if (smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
                    smt = t;
            }
            if (smt != null) {
                if (lgt.getPlayers().size() - smt.getPlayers().size() > 1 && smt.getAutoBalanceTeam()) {
                    MultiplayerType.switchTeam(mgm, ply, smt);
                    ply.sendInfoMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()));
                    mdata.sendMinigameMessage(mgm,
                            String.format(smt.getGameAutobalanceMessage(),
                                    ply.getDisplayName(mgm.usePlayerDisplayNames()), smt.getChatColor() + smt.getDisplayName()), null, ply);
                }
            }
        }
    }


    /**
     * Returns the module that is assigned to this mechanic, or null if none is assigned. This is to open the settings menu
     * for the GameMechanic if clicked in the edit menu.
     *
     * @return The module that has been assigned
     */
    public abstract MinigameModule displaySettings(Minigame minigame);

    /**
     * Called when a global Minigame has been started.
     *
     * @param minigame the game
     * @param caller   The player who initiated the global Minigame or null if not by a player.
     */
    public abstract void startMinigame(Minigame minigame, MinigamePlayer caller);

    /**
     * Called when a global Minigame has been stopped.
     *
     * @param minigame the game
     * @param caller   The player who stopped the global Minigame or null if not by a player.
     */
    public abstract void stopMinigame(Minigame minigame, MinigamePlayer caller);

    /**
     * Called when a player joins a Minigame. Called after the player has completely joined the game.
     *
     * @param minigame the game
     * @param player   the player
     */
    public abstract void onJoinMinigame(Minigame minigame, MinigamePlayer player);

    /**
     * Called when a player quits a Minigame or is forced to quit by the Minigame. Called as the quit function has started.
     *
     * @param minigame the game
     * @param player   the player
     * @param forced   true if forced
     */
    public abstract void quitMinigame(Minigame minigame, MinigamePlayer player, boolean forced);

    /**
     * Called when a player (or group of players) wins a Minigame. Called as the end function has been started, so winners and
     * losers can still be modified.
     *
     * @param minigame the game
     * @param winners  winning players
     * @param losers   losing players
     */
    public abstract void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers);
}
