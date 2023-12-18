package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.JuggernautModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        if (minigame.isTeamGame()) { // caller should not be null since that is only possible on global != multiplayer aka team game types
            MinigameMessageManager.sendMgMessage(caller, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_JUGGERNAUT_ERROR_TEAM);
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
    public void quitMinigame(@NotNull Minigame minigame, @NotNull MinigamePlayer mgPlayer,
                             boolean forced) {
        JuggernautModule juggernautModule = JuggernautModule.getMinigameModule(minigame);
        if (juggernautModule.getJuggernaut() != null && juggernautModule.getJuggernaut() == mgPlayer) {
            juggernautModule.setJuggernaut(null);

            if (!forced && minigame.getPlayers().size() > 1) {
                MinigamePlayer juggernaut = assignNewJuggernaut(minigame.getPlayers(), mgPlayer);

                if (juggernaut != null) {
                    juggernautModule.setJuggernaut(juggernaut);
                    MinigameMessageManager.sendMgMessage(juggernaut, MinigameMessageType.INFO, MinigameLangKey.PLAYER_JUGGERNAUT_PLAYERMSG);
                    mdata.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.PLAYER_JUGGERNAUT_GAMEMSG,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.PLAYER.getKey(), juggernaut.getDisplayName(minigame.usePlayerDisplayNames()))),
                            MinigameMessageType.INFO, juggernaut);
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
        if (ply.getMinigame() != null && ply.getMinigame().getMechanic() == this) {
            JuggernautModule jm = JuggernautModule.getMinigameModule(ply.getMinigame());

            if (jm.getJuggernaut() == ply) {
                if (event.getEntity().getKiller() != null) {
                    MinigamePlayer pk = pdata.getMinigamePlayer(event.getEntity().getKiller());
                    jm.setJuggernaut(pk);
                    pk.addScore();
                    pk.getMinigame().setScore(pk, pk.getScore());
                    checkScore(pk);

                } else {
                    jm.setJuggernaut(assignNewJuggernaut(ply.getMinigame().getPlayers(), ply));
                }
            } else {
                if (event.getEntity().getKiller() != null) {
                    MinigamePlayer pk = pdata.getMinigamePlayer(event.getEntity().getKiller());
                    if (jm.getJuggernaut() == pk) {
                        pk.addScore();
                        pk.getMinigame().setScore(pk, pk.getScore());
                        checkScore(pk);
                    }
                }
            }
        }
    }
}
