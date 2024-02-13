package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiplayerType extends MinigameTypeBase {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();

    public MultiplayerType() {
        setType(MinigameType.MULTIPLAYER);
    }

    public static void switchTeam(Minigame mgm, @NotNull MinigamePlayer player, @NotNull Team newTeam) {
        if (player.getTeam() != null) {
            player.removeTeam();
        }

        newTeam.addPlayer(player);
    }

    @Override
    public boolean cannotStart(@NotNull Minigame mgm, @NotNull MinigamePlayer mgPlayer) {
        if (mgm.getPlayers().size() < mgm.getMaxPlayers()) {
            if (mgm.getLobbyLocation() != null) {
                return false;
            } else {
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOLOBY);
            }
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_FULL);
        }

        return true;
    }

    @Override
    public boolean teleportOnJoin(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm) {
        Location location = mgm.getLobbyLocation();
        boolean result = false;
        if (location == null) {
            plugin.getLogger().warning("Game has no lobby set and it was expected:" + mgm.getName());
        } else {
            result = mgPlayer.teleport(location);
            if (plugin.getConfig().getBoolean("warnings") && mgPlayer.getPlayer().getWorld() != location.getWorld() &&
                    mgPlayer.getPlayer().hasPermission("minigame.set.lobby")) { //todo permission manager

                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.WARNING, MinigameLangKey.MINIGAME_WARNING_TELEPORT_ACROSS_WORLDS);
            }
        }
        return result;
    }

    @Override
    public boolean joinMinigame(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm) {
        if (!LobbySettingsModule.getMinigameModule(mgm).canInteractPlayerWait()) mgPlayer.setCanInteract(false);
        if (!LobbySettingsModule.getMinigameModule(mgm).canMovePlayerWait()) mgPlayer.setFrozen(true);
        if (!mgm.isWaitingForPlayers() && !mgm.hasStarted()) {
            if (mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()) {
                mgm.setMpTimer(new MultiplayerTimer(mgm));
                mgm.getMpTimer().startTimer();

                if (mgm.getPlayers().size() == mgm.getMaxPlayers()) {
                    mgm.getMpTimer().setCurrentLobbyWaitTime(0);
                    MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_SKIPWAITTIME));
                }
            } else if (mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()) {
                mgm.getMpTimer().setCurrentLobbyWaitTime(0);
                MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_SKIPWAITTIME));
            } else if (mgm.getMpTimer() == null) {
                int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
                MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_WAITINGFORPLAYERS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(neededPlayers))));
            }
        } else if (mgm.hasStarted()) {
            mgPlayer.setLatejoining(true);
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_LATEJOIN,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(5)))); //TODO: Late join delay variable
            final MinigamePlayer fply = mgPlayer;
            final Minigame fmgm = mgm;
            if (mgm.isTeamGame()) {
                Team smTeam = null;
                for (Team team : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    if (smTeam == null || team.getPlayers().size() < smTeam.getPlayers().size()) {
                        smTeam = team;
                    }
                }

                smTeam.addPlayer(mgPlayer);
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, MiniMessage.miniMessage().deserialize(smTeam.getPlayerAssignMessage(),
                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(smTeam.getDisplayName(), smTeam.getTextColor()))));

                final Team fteam = smTeam;
                mgPlayer.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (fply.isInMinigame()) {
                        List<Location> locs = new ArrayList<>();
                        if (TeamsModule.getMinigameModule(fmgm).hasTeamStartLocations()) {
                            locs.addAll(fteam.getStartLocations());
                        } else {
                            locs.addAll(fmgm.getStartLocations());
                        }
                        Collections.shuffle(locs);
                        fply.teleport(locs.get(0));
                        fply.getLoadout().equipLoadout(fply);
                        fply.setLatejoining(false);
                        fply.setFrozen(false);
                        fply.setCanInteract(true);
                        fply.setLateJoinTimer(-1);
                    }
                }, 5 * 20)); //TODO: Latejoin variable
            } else {
                mgPlayer.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (fply.isInMinigame()) {
                        List<Location> locs = new ArrayList<>(fmgm.getStartLocations());
                        Collections.shuffle(locs);
                        fply.teleport(locs.get(0));
                        fply.getLoadout().equipLoadout(fply);
                        fply.setLatejoining(false);
                        fply.setFrozen(false);
                        fply.setCanInteract(true);
                        fply.setLateJoinTimer(-1);
                    }
                }, 5 * 20)); //TODO: Latejoin variable
            }
            mgPlayer.getPlayer().setScoreboard(mgm.getScoreboardManager());
            mgm.setScore(mgPlayer, 1);
            mgm.setScore(mgPlayer, 0);
        }
        return true;
    }

    @Override
    public void quitMinigame(final @NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm, boolean forced) {
        int teamsWithPlayers = 0;

        if (mgm.isTeamGame()) {
            mgPlayer.removeTeam();
            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                if (!t.getPlayers().isEmpty())
                    teamsWithPlayers++;
            }

            if (mgm.getMpBets() != null && mgm.isWaitingForPlayers() && !forced) {
                if (mgm.getMpBets().getPlayersMoneyBet(mgPlayer) != null) {
                    plugin.getEconomy().depositPlayer(mgPlayer.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(mgPlayer));
                }
                mgm.getMpBets().removePlayersBet(mgPlayer);
            }
        } else {
            if (mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)) {
                if (mgm.getMpBets().getPlayersItemBet(mgPlayer) != null) {
                    final ItemStack item = mgm.getMpBets().getPlayersItemBet(mgPlayer).clone();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> mgPlayer.getPlayer().getInventory().addItem(item));
                } else if (mgm.getMpBets().getPlayersMoneyBet(mgPlayer) != null) {
                    plugin.getEconomy().depositPlayer(mgPlayer.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(mgPlayer));
                }
                mgm.getMpBets().removePlayersBet(mgPlayer);
            }
        }

        if (mgm.isTeamGame() && mgm.getPlayers().size() > 1 &&
                teamsWithPlayers == 1 && mgm.hasStarted() && !forced) {
            if (TeamsModule.getMinigameModule(mgm).getTeams().size() != 1) {
                Team winner = null;
                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    if (!t.getPlayers().isEmpty()) {
                        winner = t;
                        break;
                    }
                }
                List<MinigamePlayer> w = new ArrayList<>(winner.getPlayers());
                List<MinigamePlayer> l = new ArrayList<>();
                plugin.getPlayerManager().endMinigame(mgm, w, l);

                if (mgm.getMpBets() != null) {
                    mgm.setMpBets(null);
                }
            }
        } else if (mgm.getPlayers().size() == 2 && mgm.hasStarted() && !forced) {
            List<MinigamePlayer> w = new ArrayList<>(mgm.getPlayers());
            w.remove(mgPlayer);
            List<MinigamePlayer> l = new ArrayList<>();
            plugin.getPlayerManager().endMinigame(mgm, w, l);

            if (mgm.getMpBets() != null) {
                mgm.setMpBets(null);
            }
        } else if (mgm.getPlayers().size() - 1 < mgm.getMinPlayers() &&
                mgm.getMpTimer() != null &&
                mgm.getMpTimer().getStartWaitTimeLeft() != 0 &&
                (mgm.getState() == MinigameState.STARTING || mgm.getState() == MinigameState.WAITING)) {
            mgm.getMpTimer().setCurrentLobbyWaitTime(Minigames.getPlugin().getConfig().getInt("multiplayer.waitforplayers"));
            mgm.getMpTimer().pauseTimer();
            mgm.getMpTimer().removeTimer();
            mgm.setMpTimer(null);
            mgm.setState(MinigameState.IDLE);
            MinigameMessageManager.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_WAITINGFORPLAYERS,
                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(1))));
        }
    }

    @Override
    public void endMinigame(@NotNull List<@NotNull MinigamePlayer> winners,
                            @NotNull List<@NotNull MinigamePlayer> losers, @NotNull Minigame mgm) {
        if (mgm.isTeamGame()) {
            for (MinigamePlayer player : winners) {
                player.removeTeam();
            }
            for (MinigamePlayer player : losers) {
                player.removeTeam();
            }
            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                t.resetScore();
            }
        }

        if (mgm.getMpTimer() == null) return;
        mgm.getMpTimer().setStartWaitTime(0);
        mgm.setMpTimer(null);
    }

    public void endMinigameFindWinner(Minigame mgm) {
        if (mgm.isTeamGame()) {
            if (TeamsModule.getMinigameModule(mgm).getDefaultWinner() != null) { //default winner
                TeamsModule tm = TeamsModule.getMinigameModule(mgm);
                List<MinigamePlayer> defaultWinners;
                List<MinigamePlayer> defaultLosers;

                //if we have the default winner team on the field, make them winners
                if (TeamsModule.getMinigameModule(mgm).hasTeam(TeamsModule.getMinigameModule(mgm).getDefaultWinner())) {
                    defaultWinners = new ArrayList<>(tm.getTeam(tm.getDefaultWinner()).getPlayers().size());
                    defaultLosers = new ArrayList<>(mgm.getPlayers().size() - tm.getTeam(tm.getDefaultWinner()).getPlayers().size());

                    defaultWinners.addAll(tm.getTeam(tm.getDefaultWinner()).getPlayers());
                } else { // no one wins
                    defaultWinners = new ArrayList<>();
                    defaultLosers = new ArrayList<>(mgm.getPlayers().size());
                }

                //make all losers, that are not in default winners team
                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    if (t.getColor() != TeamsModule.getMinigameModule(mgm).getDefaultWinner())
                        defaultLosers.addAll(t.getPlayers());
                }

                pdata.endMinigame(mgm, defaultWinners, defaultLosers);

            } else { // no default winner
                List<Team> drawTeams = new ArrayList<>();
                Team winner = null;

                for (Team team : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    //make the next team winner, if they have the highest score
                    if (winner == null || (team.getScore() > winner.getScore() &&
                            (drawTeams.isEmpty() || team.getScore() > drawTeams.get(0).getScore()))) {
                        winner = team;

                        //make the next team draw with the last winner, if their scores match
                    } else if (team.getScore() == winner.getScore()) {
                        //clear lower draw teams
                        if (!drawTeams.isEmpty()) {
                            drawTeams.clear();
                        }

                        drawTeams.add(winner);
                        drawTeams.add(team);

                        //the last winner draws
                        winner = null;

                    } else if (!drawTeams.isEmpty() && drawTeams.get(0).getScore() == team.getScore()) {
                        //new team also draws
                        drawTeams.add(team);
                    }
                }

                //if we have a winner, all the other ones are losers
                if (winner != null) {
                    List<MinigamePlayer> winners = new ArrayList<>(winner.getPlayers());
                    List<MinigamePlayer> losers = new ArrayList<>(mgm.getPlayers().size() - winner.getPlayers().size());

                    //gather losers
                    for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                        if (t != winner)
                            losers.addAll(t.getPlayers());
                    }

                    pdata.endMinigame(mgm, winners, losers);
                } else { //no winner
                    List<MinigamePlayer> players = new ArrayList<>(mgm.getPlayers());

                    if (plugin.getConfig().getBoolean("multiplayer.broadcastwin")) {
                        if (drawTeams.size() == 2) {
                            MinigameMessageManager.broadcastServer(MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_END_TEAM_TIE,
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(drawTeams.get(0).getDisplayName(), drawTeams.get(0).getTextColor())),
                                    Placeholder.component(MinigamePlaceHolderKey.OTHER_TEAM.getKey(), Component.text(drawTeams.get(1).getDisplayName(), drawTeams.get(1).getTextColor())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName())
                            ), mgm, MinigameMessageType.TIE);
                        } else {
                            MinigameMessageManager.broadcastServer(MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_END_TEAM_TIECOUNT,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(drawTeams.size())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName())
                            ), mgm, MinigameMessageType.TIE);
                        }

                        //build score message
                        TextComponent.Builder scores = Component.text();
                        List<Team> teams = TeamsModule.getMinigameModule(mgm).getTeams();

                        for (int i = 0; i < teams.size(); ) {
                            scores.append(Component.text(teams.get(i).getColor().name())).append(Component.text(teams.get(i).getScore()));

                            if (++i < teams.size()) {
                                scores.append(Component.text(" : ", NamedTextColor.WHITE));
                            }
                        }

                        MinigameMessageManager.broadcastServer(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_INFO_SCORE,
                                        Placeholder.component(MinigamePlaceHolderKey.SCORE.getKey(), scores)),
                                mgm, MinigameMessageType.INFO);
                    } else { // don't broadcastServer win
                        if (drawTeams.size() == 2) {
                            MinigameMessageManager.sendMinigameMessage(mgm,
                                    MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_END_TEAM_TIE,
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(drawTeams.get(0).getDisplayName(), drawTeams.get(0).getTextColor())),
                                            Placeholder.component(MinigamePlaceHolderKey.OTHER_TEAM.getKey(), Component.text(drawTeams.get(1).getDisplayName(), drawTeams.get(1).getTextColor())),
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName())),
                                    MinigameMessageType.TIE);
                        } else {
                            MinigameMessageManager.sendMinigameMessage(mgm,
                                    MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_END_TEAM_TIECOUNT,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(drawTeams.size())),
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName())),
                                    MinigameMessageType.TIE);
                        }

                        for (MinigamePlayer mgPlayer : players) {
                            pdata.quitMinigame(mgPlayer, true);

                            //build score message
                            TextComponent.Builder scores = Component.text();
                            List<Team> teams = TeamsModule.getMinigameModule(mgm).getTeams();

                            for (int i = 0; i < teams.size(); ) {
                                scores.append(Component.text(teams.get(i).getColor().name())).append(Component.text(teams.get(i).getScore()));

                                if (++i < teams.size()) {
                                    scores.append(Component.text(" : ", NamedTextColor.WHITE));
                                }
                            }

                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_INFO_SCORE,
                                    Placeholder.component(MinigamePlaceHolderKey.SCORE.getKey(), scores));
                        }
                    }

                    //reset scores
                    for (Team team : TeamsModule.getMinigameModule(mgm).getTeams()) {
                        team.resetScore();
                    }

                    //todo figure out why the resetting happens here, but not in cases someone wins or in cases this isn't a multiplayer game
                    //reset timers
                    if (mgm.getMinigameTimer() != null) {
                        mgm.getMinigameTimer().stopTimer();
                        mgm.setMinigameTimer(null);
                    }

                    if (mgm.getMpTimer() != null) {
                        mgm.getMpTimer().setStartWaitTime(0);
                        mgm.getMpTimer().pauseTimer();
                        mgm.getMpTimer().removeTimer();
                        mgm.setMpTimer(null);
                    }

                    // reset floor degenerators
                    if (mgm.getFloorDegenerator() != null && mgm.getPlayers().isEmpty()) {
                        mgm.getFloorDegenerator().stopDegenerator();
                    }

                    //if no one wins the game, no one wins the bets
                    if (mgm.getMpBets() != null && mgm.getPlayers().isEmpty()) {
                        mgm.setMpBets(null);
                    }
                }
            }
        } else { //no team minigame
            MinigamePlayer winningPlayer = null;
            int winingScore = 0;

            for (MinigamePlayer mgPlayer : mgm.getPlayers()) {
                if (mgPlayer.getScore() > 0) {
                    if (mgPlayer.getScore() > winingScore) {
                        winningPlayer = mgPlayer;
                        winingScore = mgPlayer.getScore();

                    } else if (mgPlayer.getScore() == winingScore) {
                        if (winningPlayer != null && mgPlayer.getDeaths() < winningPlayer.getDeaths()) {
                            winningPlayer = mgPlayer;

                        } else if (winningPlayer == null) {
                            winningPlayer = mgPlayer;
                        }
                    }
                }
            }

            List<MinigamePlayer> losers = new ArrayList<>(mgm.getPlayers());
            List<MinigamePlayer> winners = new ArrayList<>();

            if (winningPlayer != null) {
                losers.remove(winningPlayer);
                winners.add(winningPlayer);
            }

            pdata.endMinigame(mgm, winners, losers);
        }
    }

    /*----------------*/
    /*-----EVENTS-----*/
    /*----------------*/

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRespawn(PlayerRespawnEvent event) {
        final MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
        if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().getType() == MinigameType.MULTIPLAYER) {
            Minigame mg = mgPlayer.getMinigame();
            Location respawnPos;
            if (mgPlayer.getMinigame().isTeamGame()) {
                Team team = mgPlayer.getTeam();
                if (mg.hasStarted() && !mgPlayer.isLatejoining()) {
                    if (mg.isAllowedMPCheckpoints() && mgPlayer.hasCheckpoint()) {
                        respawnPos = mgPlayer.getCheckpoint();
                    } else {
                        List<Location> starts = new ArrayList<>();
                        if (TeamsModule.getMinigameModule(mg).hasTeamStartLocations()) {
                            starts.addAll(team.getStartLocations());
                            mgPlayer.getLoadout().equipLoadout(mgPlayer);
                        } else {
                            starts.addAll(mg.getStartLocations());
                        }
                        Collections.shuffle(starts);
                        respawnPos = starts.get(0);
                    }
                    mgPlayer.getLoadout().equipLoadout(mgPlayer);
                } else {
                    respawnPos = mg.getLobbyLocation();
                }
            } else {
                if (mg.hasStarted() && !mgPlayer.isLatejoining()) {
                    if (mg.isAllowedMPCheckpoints() && mgPlayer.hasCheckpoint()) {
                        respawnPos = mgPlayer.getCheckpoint();
                    } else {
                        List<Location> starts = new ArrayList<>(mg.getStartLocations());
                        Collections.shuffle(starts);
                        respawnPos = starts.get(0);
                    }

                    mgPlayer.getLoadout().equipLoadout(mgPlayer);
                } else {
                    respawnPos = mg.getLobbyLocation();
                }
            }

            event.setRespawnLocation(respawnPos);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> mgPlayer.getPlayer().setNoDamageTicks(60));
        }
    }

    @EventHandler
    private void timerExpire(TimerExpireEvent event) {
        final Minigame mgm = event.getMinigame();
        if (mgm.getType() == MinigameType.MULTIPLAYER && event.getMinigame().getState() == MinigameState.STARTED) {
            endMinigameFindWinner(mgm);
        }
    }
}