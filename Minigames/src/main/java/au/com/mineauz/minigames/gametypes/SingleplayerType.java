package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.StoredPlayerCheckpoints;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class SingleplayerType extends MinigameTypeBase {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigamePlayerManager pdata = plugin.getPlayerManager();

    public SingleplayerType() {
        setType(MinigameType.SINGLEPLAYER);
    }

    @Override
    public boolean cannotStart(@NotNull Minigame mgm, @NotNull MinigamePlayer mgPlayer) {
        boolean cannotStart = mgm.isSpMaxPlayers() && mgm.getPlayers().size() >= mgm.getMaxPlayers();
        if (cannotStart) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_FULL);
        }

        return cannotStart;
    }

    @Override
    public boolean teleportOnJoin(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm) {
        List<Location> locs = new ArrayList<>(mgm.getStartLocations());

        if (locs.isEmpty()) {
            return false;
        }

        Collections.shuffle(locs);
        boolean result = mgPlayer.teleport(locs.get(0));
        if (plugin.getConfig().getBoolean("warnings") && mgPlayer.getPlayer().getWorld() != locs.get(0).getWorld() &&
                mgPlayer.getPlayer().hasPermission("minigame.set.start")) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_WARNING_TELEPORT_ACROSS_WORLDS);
        }
        return result;
    }

    @Override
    public boolean joinMinigame(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame mgm) {

        if (mgm.getLives() > 0 && Math.abs(mgm.getLives()) < Integer.MAX_VALUE) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_LIVES_LIVESLEFT,
                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(mgm.getLives())));
        }
        if (!mgm.isAllowedFlight()) {
            mgPlayer.setCanFly(false);
        } else {
            mgPlayer.setCanFly(true);
            if (mgm.isFlightEnabled())
                mgPlayer.getPlayer().setFlying(true);
        }
        if (mgPlayer.getStoredPlayerCheckpoints().hasCheckpoint(mgm.getName(false))) {
            mgPlayer.setCheckpoint(mgPlayer.getStoredPlayerCheckpoints().getCheckpoint(mgm.getName(false)));
            StoredPlayerCheckpoints spc = mgPlayer.getStoredPlayerCheckpoints();
            if (spc.hasFlags(mgm.getName(false))) {
                mgPlayer.setFlags(spc.getFlags(mgm.getName(false)));
            }
            if (spc.hasTime(mgm.getName(false))) {
                mgPlayer.setStoredTime(spc.getTime(mgm.getName(false)));
            }
            if (spc.hasDeaths(mgm.getName(false))) {
                mgPlayer.setDeaths(spc.getDeaths(mgm.getName(false)));
            }
            if (spc.hasReverts(mgm.getName(false))) {
                mgPlayer.setReverts(spc.getReverts(mgm.getName(false)));
            }
            spc.removeCheckpoint(mgm.getName(false));
            spc.removeFlags(mgm.getName(false));
            spc.removeDeaths(mgm.getName(false));
            spc.removeTime(mgm.getName(false));
            spc.removeReverts(mgm.getName(false));
            mgPlayer.teleport(mgPlayer.getCheckpoint());
            spc.saveCheckpoints();
        }

        if (mgm.getState() != MinigameState.OCCUPIED)
            mgm.setState(MinigameState.OCCUPIED);

        mgPlayer.getLoadout().equiptLoadout(mgPlayer);
        return true;
    }

    @Override
    public void endMinigame(@NotNull List<@NotNull MinigamePlayer> winners, @Nullable List<@Nullable MinigamePlayer> losers, @NotNull Minigame mgm) {
        for (MinigamePlayer player : winners) {
            if (player.getStoredPlayerCheckpoints().hasCheckpoint(mgm.getName(false))) {
                player.getStoredPlayerCheckpoints().removeCheckpoint(mgm.getName(false));
                player.getStoredPlayerCheckpoints().removeDeaths(mgm.getName(false));
                player.getStoredPlayerCheckpoints().removeFlags(mgm.getName(false));
                player.getStoredPlayerCheckpoints().removeReverts(mgm.getName(false));
                player.getStoredPlayerCheckpoints().removeTime(mgm.getName(false));
                player.getStoredPlayerCheckpoints().saveCheckpoints();
            }
        }
    }

    @Override
    public void quitMinigame(final @NotNull MinigamePlayer player, final @NotNull Minigame mgm, boolean forced) {
        if (mgm.canSaveCheckpoint()) {
            StoredPlayerCheckpoints spc = player.getStoredPlayerCheckpoints();
            spc.addCheckpoint(mgm.getName(false), player.getCheckpoint());
            if (!player.getFlags().isEmpty()) {
                spc.addFlags(mgm.getName(false), player.getFlags());
            }
            spc.addDeaths(mgm.getName(false), player.getDeaths());
            spc.addReverts(mgm.getName(false), player.getReverts());
            spc.addTime(mgm.getName(false), Calendar.getInstance().getTimeInMillis() - player.getStartTime() + player.getStoredTime());
            spc.saveCheckpoints();
        }

        if (mgm.getRecorderData().hasData() && !mgm.getPlayers().isEmpty()) {
            mgm.getRecorderData().restoreAll(player);
        }
    }

    /*----------------*/
    /*-----EVENTS-----*/
    /*----------------*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (pdata.getMinigamePlayer(event.getPlayer()).isInMinigame()) {
            MinigamePlayer mgPlayer = pdata.getMinigamePlayer(event.getPlayer());
            Minigame mgm = mgPlayer.getMinigame();
            if (mgm.getType() == MinigameType.SINGLEPLAYER) {
                event.setRespawnLocation(mgPlayer.getCheckpoint());
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_CHECKPOINT_DEATHREVERT);

                mgPlayer.getLoadout().equiptLoadout(mgPlayer);
            }
        }
    }
}
