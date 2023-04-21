package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiplayerType extends MinigameTypeBase {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();
    private final MinigameManager mdata = plugin.getMinigameManager();

    public MultiplayerType() {
        setType(MinigameType.MULTIPLAYER);
    }

    public static void switchTeam(Minigame mgm, MinigamePlayer player, Team newTeam) {
        if (player.getTeam() != null)
            player.removeTeam();
        newTeam.addPlayer(player);
    }

    @Override
    public boolean cannotStart(Minigame mgm, MinigamePlayer player) {
        String message = null;
        boolean cannotStart;
        cannotStart = mgm.getPlayers().size() >= mgm.getMaxPlayers();
        if (cannotStart) message = MinigameUtils.getLang("minigame.full");
        cannotStart = mgm.getLobbyPosition() == null;
        if (cannotStart) message = MinigameUtils.getLang("minigame.error.noLobby");
        if (cannotStart) player.sendMessage(message, MinigameMessageType.ERROR);
        return cannotStart;
    }

    @Override
    public boolean teleportOnJoin(MinigamePlayer player, Minigame mgm) {
        Location location = mgm.getLobbyPosition();
        boolean result = false;
        if (location == null) {
            plugin.getLogger().warning("Game has no lobby set and it was expected:" + mgm.getName(true));
        } else {
            result = player.teleport(location);
            if (plugin.getConfig().getBoolean("warnings") && player.getPlayer().getWorld() != location.getWorld() &&
                    player.getPlayer().hasPermission("minigame.set.lobby")) {
                player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE +
                        "Lobby location is across worlds! This may cause some server performance issues!", MinigameMessageType.ERROR);
            }
        }
        return result;
    }

    @Override
    public boolean joinMinigame(MinigamePlayer player, Minigame mgm) {
        if (!LobbySettingsModule.getMinigameModule(mgm).canInteractPlayerWait()) player.setCanInteract(false);
        if (!LobbySettingsModule.getMinigameModule(mgm).canMovePlayerWait()) player.setFrozen(true);
        if (!mgm.isWaitingForPlayers() && !mgm.hasStarted()) {
            if (mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()) {
                mgm.setMpTimer(new MultiplayerTimer(mgm));
                mgm.getMpTimer().startTimer();
                if (mgm.getPlayers().size() == mgm.getMaxPlayers()) {
                    mgm.getMpTimer().setCurrentLobbyWaitTime(0);
                    mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"));
                }
            } else if (mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()) {
                mgm.getMpTimer().setCurrentLobbyWaitTime(0);
                mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"));
            } else if (mgm.getMpTimer() == null) {
                int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
                mdata.sendMinigameMessage(mgm, MessageManager.getMinigamesMessage("minigame.waitingForPlayers", neededPlayers));
            }
        } else if (mgm.hasStarted()) {
            player.setLatejoining(true);
            player.sendInfoMessage(MessageManager.getMinigamesMessage("minigame.lateJoin", 5)); //TODO: Late join delay variable
            final MinigamePlayer fply = player;
            final Minigame fmgm = mgm;
            if (mgm.isTeamGame()) {
                Team smTeam = null;
                for (Team team : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    if (smTeam == null || team.getPlayers().size() < smTeam.getPlayers().size()) {
                        smTeam = team;
                    }
                }

                smTeam.addPlayer(player);
                player.sendMessage(String.format(smTeam.getAssignMessage(), smTeam.getChatColor() + smTeam.getDisplayName()), MinigameMessageType.INFO);

                final Team fteam = smTeam;
                player.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (fply.isInMinigame()) {
                        List<Location> locs = new ArrayList<>();
                        if (TeamsModule.getMinigameModule(fmgm).hasTeamStartLocations()) {
                            locs.addAll(fteam.getStartLocations());
                        } else {
                            locs.addAll(fmgm.getStartLocations());
                        }
                        Collections.shuffle(locs);
                        fply.teleport(locs.get(0));
                        fply.getLoadout().equiptLoadout(fply);
                        fply.setLatejoining(false);
                        fply.setFrozen(false);
                        fply.setCanInteract(true);
                        fply.setLateJoinTimer(-1);
                    }
                }, 5 * 20)); //TODO: Latejoin variable
            } else {
                player.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (fply.isInMinigame()) {
                        List<Location> locs = new ArrayList<>(fmgm.getStartLocations());
                        Collections.shuffle(locs);
                        fply.teleport(locs.get(0));
                        fply.getLoadout().equiptLoadout(fply);
                        fply.setLatejoining(false);
                        fply.setFrozen(false);
                        fply.setCanInteract(true);
                        fply.setLateJoinTimer(-1);
                    }
                }, 5 * 20)); //TODO: Latejoin variable
            }
            player.getPlayer().setScoreboard(mgm.getScoreboardManager());
            mgm.setScore(player, 1);
            mgm.setScore(player, 0);
        }
        return true;
    }

    @Override
    public void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced) {
        int teamsWithPlayers = 0;

        if (mgm.isTeamGame()) {
            player.removeTeam();
            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                if (t.getPlayers().size() > 0)
                    teamsWithPlayers++;
            }

            if (mgm.getMpBets() != null && mgm.isWaitingForPlayers() && !forced) {
                if (mgm.getMpBets().getPlayersMoneyBet(player) != null) {
                    plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(player));
                }
                mgm.getMpBets().removePlayersBet(player);
            }
        } else {
            if (mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)) {
                if (mgm.getMpBets().getPlayersItemBet(player) != null) {
                    final ItemStack item = mgm.getMpBets().getPlayersItemBet(player).clone();
                    final MinigamePlayer ply = player;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> ply.getPlayer().getInventory().addItem(item));
                } else if (mgm.getMpBets().getPlayersMoneyBet(player) != null) {
                    plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(player));
                }
                mgm.getMpBets().removePlayersBet(player);
            }
        }

        if (mgm.isTeamGame() && mgm.getPlayers().size() > 1 &&
                teamsWithPlayers == 1 && mgm.hasStarted() && !forced) {
            if (TeamsModule.getMinigameModule(mgm).getTeams().size() != 1) {
                Team winner = null;
                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                    if (t.getPlayers().size() > 0) {
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
            w.remove(player);
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
            mdata.sendMinigameMessage(mgm, MessageManager.getMinigamesMessage("minigame.waitingForPlayers", 1));
        }
    }

    @Override
    public void endMinigame(List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers, Minigame mgm) {
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

    /*----------------*/
    /*-----EVENTS-----*/
    /*----------------*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER) {
            Minigame mg = ply.getMinigame();
            Location respawnPos;
            if (ply.getMinigame().isTeamGame()) {
                Team team = ply.getTeam();
                if (mg.hasStarted() && !ply.isLatejoining()) {
                    if (mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()) {
                        respawnPos = ply.getCheckpoint();
                    } else {
                        List<Location> starts = new ArrayList<>();
                        if (TeamsModule.getMinigameModule(mg).hasTeamStartLocations()) {
                            starts.addAll(team.getStartLocations());
                            ply.getLoadout().equiptLoadout(ply);
                        } else {
                            starts.addAll(mg.getStartLocations());
                        }
                        Collections.shuffle(starts);
                        respawnPos = starts.get(0);
                    }
                    ply.getLoadout().equiptLoadout(ply);
                } else {
                    respawnPos = mg.getLobbyPosition();
                }
            } else {
                if (mg.hasStarted() && !ply.isLatejoining()) {
                    if (mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()) {
                        respawnPos = ply.getCheckpoint();
                    } else {
                        List<Location> starts = new ArrayList<>(mg.getStartLocations());
                        Collections.shuffle(starts);
                        respawnPos = starts.get(0);
                    }

                    ply.getLoadout().equiptLoadout(ply);
                } else {
                    respawnPos = mg.getLobbyPosition();
                }
            }

            event.setRespawnLocation(respawnPos);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> ply.getPlayer().setNoDamageTicks(60));
        }
    }

    @EventHandler
    public void timerExpire(TimerExpireEvent event) {
        if (event.getMinigame().getType() == MinigameType.MULTIPLAYER && event.getMinigameState() == MinigameState.STARTED) {
            if (event.getMinigame().isTeamGame()) {
                Minigame mgm = event.getMinigame();

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

                    plugin.getPlayerManager().endMinigame(mgm, defaultWinners, defaultLosers);

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
                                //todo use Message manager
                                plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MessageManager.getMinigamesMessage("player.end.team.tie",
                                        drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE,
                                        drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE,
                                        event.getMinigame().getName(true)));
                            } else {
                                //todo use Message manager
                                plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MessageManager.getMinigamesMessage("player.end.team.tieCount",
                                        drawTeams.size(),
                                        event.getMinigame().getName(true)));
                            }

                            //build score message
                            StringBuilder scores = new StringBuilder();
                            int c = 1;

                            for (Team team : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                scores.append(team.getChatColor().toString()).append(team.getScore());

                                if (c != TeamsModule.getMinigameModule(mgm).getTeams().size())
                                    scores.append(ChatColor.WHITE + " : ");
                                c++;
                            }

                            plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
                        } else { // don't broadcast win
                            for (MinigamePlayer ply : players) {
                                pdata.quitMinigame(ply, true);

                                if (drawTeams.size() == 2) {
                                    //todo use Message manager
                                    ply.sendMessage(MessageManager.getMinigamesMessage("player.end.team.tie",
                                            drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE,
                                            drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE,
                                            event.getMinigame().getName(true)), MinigameMessageType.ERROR);
                                } else {
                                    //todo use Message manager
                                    ply.sendMessage(MessageManager.getMinigamesMessage("player.end.team.tieCount",
                                            drawTeams.size(),
                                            event.getMinigame().getName(true)), MinigameMessageType.ERROR);
                                }

                                //build score message
                                StringBuilder scores = new StringBuilder();
                                int c = 1;

                                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                    scores.append(t.getChatColor().toString()).append(t.getScore());
                                    if (c != TeamsModule.getMinigameModule(mgm).getTeams().size())
                                        scores.append(ChatColor.WHITE + " : ");
                                    c++;
                                }

                                ply.sendInfoMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
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
                        if (mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0) {
                            mgm.getFloorDegenerator().stopDegenerator();
                        }

                        //if no one wins the game, no one wins the bets
                        if (mgm.getMpBets() != null && mgm.getPlayers().size() == 0) {
                            mgm.setMpBets(null);
                        }
                    }
                }
            } else { //no team minigame
                MinigamePlayer winningPlayer = null;
                int winingScore = 0;

                for (MinigamePlayer ply : event.getMinigame().getPlayers()) {
                    if (ply.getScore() > 0) {
                        if (ply.getScore() > winingScore) {
                            winningPlayer = ply;
                            winingScore = ply.getScore();

                        } else if (ply.getScore() == winingScore) {
                            if (winningPlayer != null && ply.getDeaths() < winningPlayer.getDeaths()) {
                                winningPlayer = ply;

                            } else if (winningPlayer == null) {
                                winningPlayer = ply;
                            }
                        }
                    }
                }

                List<MinigamePlayer> losers = new ArrayList<>(event.getMinigame().getPlayers());
                List<MinigamePlayer> winners = new ArrayList<>();

                if (winningPlayer != null) {
                    losers.remove(winningPlayer);
                    winners.add(winningPlayer);
                }

                pdata.endMinigame(event.getMinigame(), winners, losers);
            }
        }
    }
}