package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.events.TimerExpireEvent;
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
    private static Minigames plugin = Minigames.getPlugin();
    private MinigamePlayerManager pdata = plugin.getPlayerManager();
    private MinigameManager mdata = plugin.getMinigameManager();

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
                mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers));
            }
        } else if (mgm.hasStarted()) {
            player.setLatejoining(true);
            player.sendInfoMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
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
                if (mgm.getMpBets().getPlayersBet(player) != null) {
                    final ItemStack item = mgm.getMpBets().getPlayersBet(player).clone();
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
            mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", 1));
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
                if (TeamsModule.getMinigameModule(mgm).getDefaultWinner() != null) {
                    TeamsModule tm = TeamsModule.getMinigameModule(mgm);
                    List<MinigamePlayer> w;
                    List<MinigamePlayer> l;
                    if (TeamsModule.getMinigameModule(mgm).hasTeam(TeamsModule.getMinigameModule(mgm).getDefaultWinner())) {
                        w = new ArrayList<>(tm.getTeam(tm.getDefaultWinner()).getPlayers().size());
                        l = new ArrayList<>(mgm.getPlayers().size() - tm.getTeam(tm.getDefaultWinner()).getPlayers().size());
                        w.addAll(tm.getTeam(tm.getDefaultWinner()).getPlayers());
                    } else {
                        w = new ArrayList<>();
                        l = new ArrayList<>(mgm.getPlayers().size());
                    }

                    for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                        if (t.getColor() != TeamsModule.getMinigameModule(mgm).getDefaultWinner())
                            l.addAll(t.getPlayers());
                    }
                    plugin.getPlayerManager().endMinigame(mgm, w, l);
                } else {
                    List<Team> drawTeams = new ArrayList<>();
                    Team winner = null;
                    for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                        if (winner == null || (t.getScore() > winner.getScore() &&
                                (drawTeams.isEmpty() || t.getScore() > drawTeams.get(0).getScore()))) {
                            winner = t;
                        } else if (winner != null && t.getScore() == winner.getScore()) {
                            if (!drawTeams.isEmpty()) {
                                drawTeams.clear();
                            }
                            drawTeams.add(winner);
                            drawTeams.add(t);
                            winner = null;
                        } else if (!drawTeams.isEmpty() && drawTeams.get(0).getScore() == t.getScore()) {
                            drawTeams.add(t);
                        }
                    }

                    if (winner != null) {
                        List<MinigamePlayer> w = new ArrayList<>(winner.getPlayers());
                        List<MinigamePlayer> l = new ArrayList<>(mgm.getPlayers().size() - winner.getPlayers().size());
                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                            if (t != winner)
                                l.addAll(t.getPlayers());
                        }
                        pdata.endMinigame(mgm, w, l);
                    } else {
                        List<MinigamePlayer> players = new ArrayList<>(mgm.getPlayers());
                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                            t.resetScore();
                        }

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

                        if (mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0) {
                            mgm.getFloorDegenerator().stopDegenerator();
                        }

                        if (mgm.getMpBets() != null && mgm.getPlayers().size() == 0) {
                            mgm.setMpBets(null);
                        }

                        for (MinigamePlayer ply : players) {
                            pdata.quitMinigame(ply, true);
                            if (!plugin.getConfig().getBoolean("multiplayer.broadcastwin")) {
                                if (drawTeams.size() == 2) {
                                    ply.sendMessage(MinigameUtils.formStr("player.end.team.tie",
                                            drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE,
                                            drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE,
                                            event.getMinigame().getName(true)), MinigameMessageType.ERROR);
                                } else {
                                    ply.sendMessage(MinigameUtils.formStr("player.end.team.tieCount",
                                            drawTeams.size(),
                                            event.getMinigame().getName(true)), MinigameMessageType.ERROR);
                                }
                                String scores = "";
                                int c = 1;
                                for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                    scores += t.getChatColor().toString() + t.getScore();
                                    if (c != TeamsModule.getMinigameModule(mgm).getTeams().size())
                                        scores += ChatColor.WHITE + " : ";
                                    c++;
                                }
                                ply.sendInfoMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
                            }
                        }
                        if (plugin.getConfig().getBoolean("multiplayer.broadcastwin")) {
                            if (drawTeams.size() == 2) {
                                plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tie",
                                        drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE,
                                        drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE,
                                        event.getMinigame().getName(true)));
                            } else {
                                plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tieCount",
                                        drawTeams.size(),
                                        event.getMinigame().getName(true)));
                            }

                            String scores = "";
                            int c = 1;
                            for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                                scores += t.getChatColor().toString() + t.getScore();
                                if (c != TeamsModule.getMinigameModule(mgm).getTeams().size())
                                    scores += ChatColor.WHITE + " : ";
                                c++;
                            }
                            plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
                        }
                    }
                }
            } else {
                MinigamePlayer player = null;
                int score = 0;
                for (MinigamePlayer ply : event.getMinigame().getPlayers()) {
                    if (ply.getScore() > 0) {
                        if (ply.getScore() > score) {
                            player = ply;
                            score = ply.getScore();
                        } else if (ply.getScore() == score) {
                            if (player != null && ply.getDeaths() < player.getDeaths()) {
                                player = ply;
                            } else if (player == null) {
                                player = ply;
                            }
                        }
                    }
                }
                List<MinigamePlayer> losers = new ArrayList<>(event.getMinigame().getPlayers());
                List<MinigamePlayer> winners = new ArrayList<>();
                if (player != null) {
                    losers.remove(player);
                    winners.add(player);
                }

                pdata.endMinigame(event.getMinigame(), winners, losers);
            }
        }
    }

}
