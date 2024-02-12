package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameTypeBase;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EndCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "end";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"stop"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_END_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_END_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.end";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (sender.hasPermission("minigame.end.other")) {
            if (args.length == 0 && sender instanceof Player player) {
                forceEndForPlayer(player, sender, MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_SELF));
            } else if (args.length > 0) {
                Player playerToEnd = Bukkit.getPlayer(args[0]);

                if (playerToEnd != null) {
                    forceEndForPlayer(playerToEnd, sender, MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_PLAYER,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), playerToEnd.displayName())));
                } else { // first argument was not a player. is it a minigame?
                    Minigame minigame = PLUGIN.getMinigameManager().getMinigame(args[0]);

                    if (minigame != null) {
                        MinigameTypeBase type = PLUGIN.getMinigameManager().minigameType(minigame.getType());

                        switch (minigame.getType()) {
                            case GLOBAL -> forceEndForGlobal(sender, minigame);
                            case MULTIPLAYER -> {
                                if (minigame.hasPlayers()) {
                                    if (args.length > 1 && minigame.isTeamGame()) { // is the second argument a team?
                                        TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
                                        TeamColor color = TeamColor.matchColor(args[1]);
                                        Team teamToWin;

                                        if (color != null && (teamToWin = teamsModule.getTeam(color)) != null) { // teams module gets checked in isTeamGame()
                                            forceEndForTeam(sender, teamToWin, minigame);
                                        } else {
                                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTEAM,
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), args[1]));
                                            return false;
                                        }
                                    } else { // default winners
                                        ((MultiplayerType) type).endMinigameFindWinner(minigame);

                                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_MINIGAME,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                                    }
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_ISEMPTY);
                                }
                            }
                            case SINGLEPLAYER -> {
                                /* Don't make the mistake to believe single player means there is only one player at time in the game
                                 * it only means that these players do not play against / with each other.
                                 * For example multiple players can do the same jump and run
                                 */
                                if (minigame.hasPlayers()) {
                                    for (MinigamePlayer mgPlayer : minigame.getPlayers()) {
                                        PLUGIN.getPlayerManager().endMinigame(mgPlayer);
                                    }

                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_MINIGAME,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_ISEMPTY);
                                }
                            }
                        } // switch
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
                        return false;
                    }
                } // end else first argument type
            }
            return true;
        } else if (sender instanceof Player player) { // does not have permission to end minigame for others, quit self if possible
            if (sender.hasPermission("minigame.quit")) {
                MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);

                if (mgPlayer.isInMinigame()) {
                    PLUGIN.getPlayerManager().quitMinigame(mgPlayer, false);
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_SELF);
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
            }
        } else if (args.length > 0) { // not a player. return proper message
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
            return false;
        }

        return true;
    }

    private void forceEndForPlayer(Player player, @NotNull CommandSender sender, @NotNull Component notInGameMessage) {
        MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);
        Minigame minigame = mgPlayer.getMinigame();

        if (minigame != null) {
            switch (minigame.getType()) {
                case GLOBAL -> forceEndForGlobal(sender, minigame);
                case MULTIPLAYER -> {
                    if (minigame.isTeamGame()) { // declare team of player to winners
                        Team teamToWin = mgPlayer.getTeam();

                        forceEndForTeam(sender, teamToWin, minigame);// teams module gets checked in isTeamGame()
                    } else { // just the one player wins, everyone else looses
                        List<MinigamePlayer> winners = List.of(mgPlayer);
                        List<MinigamePlayer> loosers = new ArrayList<>(minigame.getPlayers());
                        loosers.remove(mgPlayer);

                        PLUGIN.getPlayerManager().endMinigame(minigame, winners, loosers);
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_WINNER,
                                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), mgPlayer.displayName()));
                    }
                }
                case SINGLEPLAYER -> {
                    PLUGIN.getPlayerManager().endMinigame(mgPlayer);
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_WINNER,
                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), mgPlayer.displayName()));
                }
            }
        } else {
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.ERROR, notInGameMessage);
        }
    }

    /**
     * Needs to minigame.isTeamGame() to happen fist
     */
    private void forceEndForTeam(@NotNull CommandSender sender, @NotNull Team teamToWin, @NotNull Minigame minigame) {
        List<MinigamePlayer> winners = new ArrayList<>(teamToWin.getPlayers());
        TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

        List<MinigamePlayer> losers = new ArrayList<>();
        for (Team team : teamsModule.getTeams()) {
            if (team != teamToWin) {
                losers.addAll(team.getPlayers());
            }
        }
        PLUGIN.getPlayerManager().endMinigame(minigame, winners, losers);
        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_WINNER,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), teamToWin.getColor().getCompName()));
    }

    private void forceEndForGlobal(@NotNull CommandSender sender, Minigame minigame) {
        if (sender.hasPermission("minigame.stop")) {
            if (minigame.isEnabled()) {
                PLUGIN.getMinigameManager().stopGlobalMinigame(minigame, sender);

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_END_SUCCESS_MINIGAME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTSTARTED);
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
        }
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            // players in minigames
            List<String> result = PLUGIN.getServer().getOnlinePlayers().stream().
                    filter(player -> PLUGIN.getPlayerManager().getMinigamePlayer(player).isInMinigame()).
                    map(Player::getName).
                    collect(Collectors.toCollection(ArrayList::new));
            // minigames
            result.addAll(PLUGIN.getMinigameManager().getAllMinigames().keySet());

            return MinigameUtils.tabCompleteMatch(result, args[0]);
        } else if (args.length == 2) {
            Minigame minigame = PLUGIN.getMinigameManager().getMinigame(args[0]);

            if (minigame != null) {
                if (minigame.isTeamGame()) {
                    TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
                    List<String> result = teamsModule.getTeams().stream().map(team -> team.getColor().toString()).toList();

                    return MinigameUtils.tabCompleteMatch(result, args[1]);
                }
            }
        }

        return null;
    }
}
