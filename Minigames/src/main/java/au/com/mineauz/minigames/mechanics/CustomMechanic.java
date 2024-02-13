package au.com.mineauz.minigames.mechanics;

import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class CustomMechanic extends GameMechanicBase {

    @Override
    public String getMechanicName() {
        return "custom";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER, MinigameType.SINGLEPLAYER);
    }

    @Override
    public boolean checkCanStart(@NotNull Minigame minigame, @Nullable MinigamePlayer caller) {
        return true;
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer caller) {
    }

    @Override
    public void stopMinigame(Minigame minigame) {
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
    public void playerAutoBalance(PlayerDeathEvent event) {
        MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getEntity());
        if (mgPlayer.isInMinigame() && mgPlayer.getMinigame().isTeamGame()) {
            Minigame mgm = mgPlayer.getMinigame();

            if (mgm.getMechanicName().equals("custom")) {
                autoBalanceOnDeath(mgPlayer, mgm);
            }
        }
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return null;
    }
}
