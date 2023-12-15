package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class InfectionMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return "infection";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        if (!minigame.isTeamGame() ||
                TeamsModule.getMinigameModule(minigame).getTeams().size() != 2 ||
                !TeamsModule.getMinigameModule(minigame).hasTeam(InfectionModule.getMinigameModule(minigame).getInfectedTeam()) ||
                !TeamsModule.getMinigameModule(minigame).hasTeam(InfectionModule.getMinigameModule(minigame).getSurvivorTeam())) {
            if (caller != null) {
                caller.sendMessage(MinigameMessageManager.getUnformattedMgMessage("minigame.error.noInfection"), MinigameMessageType.ERROR);
            }
            return false;
        }
        return true;
    }

    @Override
    public List<MinigamePlayer> balanceTeam(@NotNull List<MinigamePlayer> players, @NotNull Minigame minigame) {
        List<MinigamePlayer> result = new ArrayList<>();
        Collections.shuffle(players);
        for (MinigamePlayer ply : players) {
            Team infectedTeam = TeamsModule.getMinigameModule(minigame).getTeam(InfectionModule.getMinigameModule(minigame).getInfectedTeam());
            Team survivorTeam = TeamsModule.getMinigameModule(minigame).getTeam(InfectionModule.getMinigameModule(minigame).getSurvivorTeam());
            Team team = ply.getTeam();
            double percent = ((Integer) InfectionModule.getMinigameModule(minigame).getInfectedPercent()).doubleValue() / 100d;
            if (team == survivorTeam) {
                if (infectedTeam.getPlayers().size() < Math.ceil(players.size() * percent) && infectedTeam.hasRoom()) {
                    MultiplayerType.switchTeam(minigame, ply, infectedTeam);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(infectedTeam.getUnformattedAssignMessage(), infectedTeam.getTextColor() + infectedTeam.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(infectedTeam.getGameAssignMessage(), ply.getName(), infectedTeam.getTextColor() + infectedTeam.getDisplayName()), null, ply);
                }
            } else if (team == null) {
                if (infectedTeam.getPlayers().size() < Math.ceil(players.size() * percent) && infectedTeam.hasRoom()) {
                    infectedTeam.addPlayer(ply);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(infectedTeam.getUnformattedAssignMessage(), infectedTeam.getTextColor() + infectedTeam.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(infectedTeam.getGameAssignMessage(), ply.getName(), infectedTeam.getTextColor() + infectedTeam.getDisplayName()), null, ply);
                } else if (survivorTeam.hasRoom()) {
                    survivorTeam.addPlayer(ply);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(survivorTeam.getUnformattedAssignMessage(), survivorTeam.getTextColor() + survivorTeam.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(survivorTeam.getGameAssignMessage(), ply.getName(), survivorTeam.getTextColor() + survivorTeam.getDisplayName()), null, ply);
                } else {
                    pdata.quitMinigame(ply, false);
                    ply.sendMessage(MinigameUtils.getLang("minigame.full"), MinigameMessageType.ERROR);
                }
            }
        }
        return result;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return InfectionModule.getMinigameModule(minigame);
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer caller) {
    }

    @Override
    public void stopMinigame(Minigame minigame, MinigamePlayer caller) {
    }

    @Override
    public void onJoinMinigame(Minigame minigame, MinigamePlayer player) {
    }

    @Override
    public void quitMinigame(Minigame minigame, MinigamePlayer player,
                             boolean forced) {
        if (InfectionModule.getMinigameModule(minigame).isInfectedPlayer(player)) {
            InfectionModule.getMinigameModule(minigame).removeInfectedPlayer(player);
        }
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
        List<MinigamePlayer> wins = new ArrayList<>(winners);
        for (MinigamePlayer ply : wins) {
            if (InfectionModule.getMinigameModule(minigame).isInfectedPlayer(ply)) {
                winners.remove(ply);
                losers.add(ply);
                InfectionModule.getMinigameModule(minigame).removeInfectedPlayer(ply);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void playerDeath(PlayerDeathEvent event) {
        MinigamePlayer player = pdata.getMinigamePlayer(event.getEntity());
        if (player.isInMinigame()) {
            Minigame mgm = player.getMinigame();
            if (mgm.isTeamGame() && mgm.getMechanicName().equals("infection")) {
                Team survivorTeam = TeamsModule.getMinigameModule(mgm).getTeam(InfectionModule.getMinigameModule(mgm).getSurvivorTeam());
                Team infectedTeam = TeamsModule.getMinigameModule(mgm).getTeam(InfectionModule.getMinigameModule(mgm).getInfectedTeam());
                if (survivorTeam.getPlayers().contains(player)) {
                    if (!infectedTeam.hasRoom()) {
                        MultiplayerType.switchTeam(mgm, player, infectedTeam);
                        InfectionModule.getMinigameModule(mgm).addInfectedPlayer(player);
                        if (event.getEntity().getKiller() != null) {
                            MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                            killer.addScore();
                            mgm.setScore(killer, killer.getScore());
                        }
                        player.resetScore();
                        mgm.setScore(player, player.getScore());

                        if (mgm.getLives() != player.getDeaths()) {
                            mdata.sendMinigameMessage(mgm, String.format(infectedTeam.getGameAssignMessage(), player.getName(), infectedTeam.getTextColor() + infectedTeam.getDisplayName()), MinigameMessageType.ERROR);
                        }
                        if (survivorTeam.getPlayers().isEmpty()) {
                            List<MinigamePlayer> w;
                            List<MinigamePlayer> l;
                            w = new ArrayList<>(infectedTeam.getPlayers());
                            l = new ArrayList<>();
                            pdata.endMinigame(mgm, w, l);
                        }
                    } else {
                        pdata.quitMinigame(player, false);
                    }
                } else {
                    if (event.getEntity().getKiller() != null) {
                        MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                        killer.addScore();
                        mgm.setScore(killer, killer.getScore());
                    }
                }
            }
        }
    }
}
