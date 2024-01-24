package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class LivesMechanic extends GameMechanicBase {

    @Override
    public String getMechanic() {
        return "lives";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        if (minigame.getLives() > 0) {
            return true;
        }
        caller.sendMessage("The Minigame must have more than 0 lives to use this type", MinigameMessageType.ERROR);
        return false;
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
    private void minigameStart(StartMinigameEvent event) {
        if (event.getMinigame().getMechanicName().equals(getMechanic())) {
            final List<MinigamePlayer> players = event.getPlayers();
            final Minigame minigame = event.getMinigame();
            for (MinigamePlayer player : players) {
                if (Math.abs(minigame.getLives()) < Integer.MAX_VALUE) {
                    int lives = minigame.getLives();
                    player.setScore(lives);
                    minigame.setScore(player, lives);
                } else {
                    player.setScore(Integer.MAX_VALUE);
                    minigame.setScore(player, Integer.MAX_VALUE);
                }
            }
        }
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(event.getEntity());
        if (ply.isInMinigame() && ply.getMinigame().getMechanicName().equals(getMechanic())) {
            ply.addScore(-1);
            ply.getMinigame().setScore(ply, ply.getScore());
        }
    }

}
