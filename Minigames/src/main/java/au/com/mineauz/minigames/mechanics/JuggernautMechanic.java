package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.JuggernautModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class JuggernautMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return "juggernaut";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(Minigame minigame, MinigamePlayer caller) {
        if (minigame.isTeamGame()) {
            caller.sendMessage("Juggernaut cannot be a team Minigame!", MinigameMessageType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return JuggernautModule.getMinigameModule(minigame);
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
        JuggernautModule jm = JuggernautModule.getMinigameModule(minigame);
        if (jm.getJuggernaut() != null && jm.getJuggernaut() == player) {
            jm.setJuggernaut(null);

            if (!forced && minigame.getPlayers().size() > 1) {
                MinigamePlayer j = assignNewJuggernaut(minigame.getPlayers(), player);

                if (j != null) {
                    jm.setJuggernaut(j);
                    j.sendInfoMessage(MinigameUtils.getLang("player.juggernaut.plyMsg"));
                    mdata.sendMinigameMessage(minigame,
                            MinigameUtils.formStr("player.juggernaut.gameMsg", j.getDisplayName(minigame.usePlayerDisplayNames())), null, j);
                }
            }
        }

        if (minigame.getPlayers().size() == 1) {
            if (minigame.getScoreboardManager().getTeam("juggernaut") != null)
                minigame.getScoreboardManager().getTeam("juggernaut").unregister();
        }
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners,
                            List<MinigamePlayer> losers) {
        JuggernautModule.getMinigameModule(minigame).setJuggernaut(null);

        minigame.getScoreboardManager().getTeam("juggernaut").unregister();
    }

    private MinigamePlayer assignNewJuggernaut(List<MinigamePlayer> players, MinigamePlayer exclude) {
        List<MinigamePlayer> plys = new ArrayList<>(players);
        if (exclude != null) {
            plys.remove(exclude);
        }
        Collections.shuffle(plys);

        return plys.get(0);
    }

    private void checkScore(MinigamePlayer ply) {
        if (ply.getScore() >= ply.getMinigame().getMaxScorePerPlayer()) {
            List<MinigamePlayer> winners = new ArrayList<>();
            winners.add(ply);
            List<MinigamePlayer> losers = new ArrayList<>(ply.getMinigame().getPlayers());
            losers.remove(ply);
            pdata.endMinigame(ply.getMinigame(), winners, losers);
        }
    }

    @EventHandler
    private void minigameStart(StartMinigameEvent event) {
        if (event.getMinigame().getMechanic() == this) {
            Minigame mgm = event.getMinigame();

            mgm.getScoreboardManager().registerNewTeam("juggernaut");
            mgm.getScoreboardManager().getTeam("juggernaut").setPrefix(ChatColor.RED.toString());

            MinigamePlayer j = assignNewJuggernaut(event.getPlayers(), null);
            JuggernautModule.getMinigameModule(event.getMinigame()).setJuggernaut(j);
        }
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        if (ply == null) return;
        if (ply.getMinigame() != null && ply.getMinigame().getMechanic() == this) {
            JuggernautModule jm = JuggernautModule.getMinigameModule(ply.getMinigame());

            if (jm.getJuggernaut() == ply) {
                if (event.getEntity().getKiller() != null) {
                    MinigamePlayer pk = pdata.getMinigamePlayer(event.getEntity().getKiller());
                    if (pk != null) {
                        jm.setJuggernaut(pk);
                        pk.addScore();
                        pk.getMinigame().setScore(pk, pk.getScore());
                        checkScore(pk);
                    } else {
                        jm.setJuggernaut(assignNewJuggernaut(ply.getMinigame().getPlayers(), ply));
                    }
                } else {
                    jm.setJuggernaut(assignNewJuggernaut(ply.getMinigame().getPlayers(), ply));
                }
            } else {
                if (event.getEntity().getKiller() != null) {
                    MinigamePlayer pk = pdata.getMinigamePlayer(event.getEntity().getKiller());
                    if (pk != null && jm.getJuggernaut() == pk) {
                        pk.addScore();
                        pk.getMinigame().setScore(pk, pk.getScore());
                        checkScore(pk);
                    }
                }
            }
        }
    }
}
