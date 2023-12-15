package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class GameMechanicBase implements Listener {
    public static Minigames plugin;
    public final MinigamePlayerManager pdata;
    public final MinigameManager mdata;

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
    public abstract boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller); //todo better return value to indicate what went wrong

    /**
     * In the case of a Minigame having teams, this should be used to balance players
     * to a specific team, usual games is evenly distributed, in the case of Infection,
     * only a specific percentage is assigned to one team by default. The default function
     * will assign teams automatically unless overridden.
     * Additionally, teams that are flagged as autoBalance false will not have players removed or added through a team switch...
     *
     * @param players  The players to be balanced to a team
     * @param minigame The minigame in which the balancing occours
     * @return List of {@link MinigamePlayer} that have been moved to a different or new team.
     */

    public List<MinigamePlayer> balanceTeam(@NotNull List<@NotNull MinigamePlayer> players, @NotNull Minigame minigame) {
        List<MinigamePlayer> result = new ArrayList<>();
        if (minigame.isTeamGame()) {
            boolean sorted = false;
            for (MinigamePlayer mgPlayer : players) {
                if (mgPlayer.getTeam() == null) {
                    Team teamToJoin = null;
                    for (Team teamToCheck : TeamsModule.getMinigameModule(minigame).getTeams()) {
                        if (teamToJoin == null || (teamToCheck.getPlayers().size() < teamToJoin.getPlayers().size() &&
                                (teamToCheck.getMaxPlayers() == 0 || teamToCheck.getPlayers().size() != teamToCheck.getMaxPlayers())))
                            teamToJoin = teamToCheck;
                    }
                    if (teamToJoin == null) {
                        pdata.quitMinigame(mgPlayer, false);
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_FULL);
                    } else {
                        teamToJoin.addPlayer(mgPlayer);
                        broadcastAutobalance(minigame, mgPlayer, teamToJoin);
                    }
                }
            }

            while (!sorted) {
                Team teamToJoin = null;
                Team teamToBalance = null;
                for (Team teamToCheck : TeamsModule.getMinigameModule(minigame).getTeams()) {
                    if (teamToJoin == null || (teamToCheck.getPlayers().size() < teamToJoin.getPlayers().size() - 1 && teamToCheck.hasRoom() && teamToCheck.getAutoBalanceTeam()))
                        teamToJoin = teamToCheck;
                    if ((teamToBalance == null || (teamToCheck.getPlayers().size() > teamToBalance.getPlayers().size() && teamToCheck.hasRoom())) && teamToCheck != teamToJoin && teamToCheck.getAutoBalanceTeam())
                        teamToBalance = teamToCheck;
                }
                if (teamToJoin != null && teamToBalance != null && teamToBalance.getPlayers().size() - teamToJoin.getPlayers().size() > 1) {
                    MinigamePlayer mgPlayer = teamToBalance.getPlayers().get(0);
                    MultiplayerType.switchTeam(minigame, mgPlayer, teamToJoin);
                    result.add(mgPlayer);


                    teamToJoin.addPlayer(mgPlayer);
                    broadcastAutobalance(minigame, mgPlayer, teamToJoin);
                } else {
                    sorted = true;
                }
            }
        }
        return result;
    }

    private void broadcastAutobalance(@NotNull Minigame minigame, @NotNull MinigamePlayer mgPlayer, @NotNull Team teamToJoin) {
        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(
                teamToJoin.getAutobalanceMessage(),
                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(teamToJoin.getDisplayName(), teamToJoin.getTextColor())))); //todo is NOT backwards compatible!!

        mdata.sendMinigameMessage(minigame,
                MiniMessage.miniMessage().deserialize(teamToJoin.getGameAutobalanceMessage(),
                        Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.getDisplayName(minigame.usePlayerDisplayNames())),
                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(teamToJoin.getDisplayName(), teamToJoin.getTextColor()))) //todo is NOT backwards compatible!!
                , null, mgPlayer);
    }

    void autoBalanceOnDeath(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm) {
        Team teamToJoin = null;
        Team teamToBalance = mgPlayer.getTeam();
        if (teamToBalance.getAutoBalanceTeam()) {//this team is flagged as  balanced - players will be removed.
            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                if (teamToJoin == null || t.getPlayers().size() < teamToJoin.getPlayers().size() - 1)
                    teamToJoin = t;
            }
            if (teamToJoin != null) {
                if (teamToBalance.getPlayers().size() - teamToJoin.getPlayers().size() > 1 && teamToJoin.getAutoBalanceTeam()) {
                    MultiplayerType.switchTeam(mgm, mgPlayer, teamToJoin);


                    teamToJoin.addPlayer(mgPlayer);
                    broadcastAutobalance(mgm, mgPlayer, teamToJoin);
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
