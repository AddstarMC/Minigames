package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PlayerKillsMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return GameMechanics.MG_MECHANICS.KILLS.toString();
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return null;
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
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
    }

    @EventHandler
    private void playerAttackPlayer(@NotNull PlayerDeathEvent event) {
        MinigamePlayer mgPlayerWhoDied = pdata.getMinigamePlayer(event.getEntity());
        Minigame mgm = mgPlayerWhoDied.getMinigame();
        if (mgPlayerWhoDied.isInMinigame() && mgm.getMechanicName().equals("kills")) {
            MinigamePlayer attacker;
            if (mgPlayerWhoDied.getPlayer().getKiller() != null) {
                attacker = pdata.getMinigamePlayer(mgPlayerWhoDied.getPlayer().getKiller());
                if (attacker == mgPlayerWhoDied) {
                    return;
                }
            } else {
                return;
            }

            if (!mgm.equals(attacker.getMinigame())) {
                return;
            }

            if (mgPlayerWhoDied.getTeam() == null) {
                attacker.addScore();
                mgm.setScore(attacker, attacker.getScore());

                if (mgm.getMaxScore() != 0 && attacker.getScore() >= mgm.getMaxScorePerPlayer()) {
                    List<MinigamePlayer> losers = new ArrayList<>(mgm.getPlayers().size() - 1);
                    List<MinigamePlayer> winner = new ArrayList<>(1);
                    winner.add(attacker);
                    for (MinigamePlayer player : mgm.getPlayers()) {
                        if (player != attacker)
                            losers.add(player);
                    }
                    pdata.endMinigame(mgm, winner, losers);
                }
            } else {
                Team team = mgPlayerWhoDied.getTeam();
                Team ateam = attacker.getTeam();

                if (team != ateam) {
                    attacker.addScore();
                    mgm.setScore(attacker, attacker.getScore());

                    ateam.addScore();
                    if (mgm.getMaxScore() != 0 && mgm.getMaxScorePerPlayer() <= ateam.getScore()) {
                        mdata.sendMinigameMessage(mgm, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_KILLS_FINALKILL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), attacker.getName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.OTHER_PLAYER.getKey(), mgPlayerWhoDied.getName())));

                        List<MinigamePlayer> w = new ArrayList<>(ateam.getPlayers());
                        List<MinigamePlayer> l = new ArrayList<>(mgm.getPlayers().size() - ateam.getPlayers().size());
                        for (Team t : TeamsModule.getMinigameModule(mgm).getTeams()) {
                            if (t != ateam)
                                l.addAll(t.getPlayers());
                        }
                        plugin.getPlayerManager().endMinigame(mgm, w, l);
                    }
                }
            }
        }
    }

    @EventHandler
    private void playerSuicide(PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply.isInMinigame() &&
                (ply.getPlayer().getKiller() == null || ply.getPlayer().getKiller() == ply.getPlayer()) &&
                ply.getMinigame().hasStarted()) {
            Minigame mgm = ply.getMinigame();
            if (mgm.getMechanicName().equals("kills")) {
                ply.takeScore();
                mgm.setScore(ply, ply.getScore());
                if (mgm.isTeamGame())
                    ply.getTeam().setScore(ply.getTeam().getScore() - 1);
            }
        }
    }

    @EventHandler
    public void playerAutoBalance(PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply.isInMinigame() && ply.getMinigame().isTeamGame()) {
            Minigame mgm = ply.getMinigame();

            if (mgm.getMechanicName().equals("kills")) {
                autoBalanceOnDeath(ply, mgm);
            }
        }
    }
}
