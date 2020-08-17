package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

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
    public boolean checkCanStart(Minigame minigame, MinigamePlayer caller) {
        if (!minigame.isTeamGame() ||
                TeamsModule.getMinigameModule(minigame).getTeams().size() != 2 ||
                !TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.RED) ||
                !TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.BLUE)) {
            if (caller != null)
                caller.sendMessage(MinigameUtils.getLang("minigame.error.noInfection"), MinigameMessageType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    public List<MinigamePlayer> balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
        List<MinigamePlayer> result = new ArrayList<>();
        Collections.shuffle(players);
        for (MinigamePlayer ply : players) {
            Team red = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.RED);
            Team blue = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.BLUE);
            Team team = ply.getTeam();
            Double percent = ((Integer) InfectionModule.getMinigameModule(minigame).getInfectedPercent()).doubleValue() / 100d;
            if (team == blue) {
                if (red.getPlayers().size() < Math.ceil(players.size() * percent) && !red.isFull()) {
                    MultiplayerType.switchTeam(minigame, ply, red);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(red.getAssignMessage(), red.getChatColor() + red.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(red.getGameAssignMessage(), ply.getName(), red.getChatColor() + red.getDisplayName()), null, ply);
                }
            } else if (team == null) {
                if (red.getPlayers().size() < Math.ceil(players.size() * percent) && !red.isFull()) {
                    red.addPlayer(ply);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(red.getAssignMessage(), red.getChatColor() + red.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(red.getGameAssignMessage(), ply.getName(), red.getChatColor() + red.getDisplayName()), null, ply);
                } else if (!blue.isFull()) {
                    blue.addPlayer(ply);
                    result.add(ply);
                    ply.sendInfoMessage(String.format(blue.getAssignMessage(), blue.getChatColor() + blue.getDisplayName()));
                    mdata.sendMinigameMessage(minigame, String.format(blue.getGameAssignMessage(), ply.getName(), blue.getChatColor() + blue.getDisplayName()), null, ply);
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
        if (player == null) return;
        if (player.isInMinigame()) {
            Minigame mgm = player.getMinigame();
            if (mgm.isTeamGame() && mgm.getMechanicName().equals("infection")) {
                Team blue = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.BLUE);
                Team red = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.RED);
                if (blue.getPlayers().contains(player)) {
                    if (!red.isFull()) {
                        MultiplayerType.switchTeam(mgm, player, red);
                        InfectionModule.getMinigameModule(mgm).addInfectedPlayer(player);
                        if (event.getEntity().getKiller() != null) {
                            MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                            killer.addScore();
                            mgm.setScore(killer, killer.getScore());
                        }
                        player.resetScore();
                        mgm.setScore(player, player.getScore());

                        if (mgm.getLives() != player.getDeaths()) {
                            mdata.sendMinigameMessage(mgm, String.format(red.getGameAssignMessage(), player.getName(), red.getChatColor() + red.getDisplayName()), MinigameMessageType.ERROR);
                        }
                        if (blue.getPlayers().isEmpty()) {
                            List<MinigamePlayer> w;
                            List<MinigamePlayer> l;
                            w = new ArrayList<>(red.getPlayers());
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
