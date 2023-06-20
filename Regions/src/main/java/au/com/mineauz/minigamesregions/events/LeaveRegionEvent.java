package au.com.mineauz.minigamesregions.events;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LeaveRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final MinigamePlayer player;
    private final Region region;

    public LeaveRegionEvent(MinigamePlayer player, Region region) {
        this.player = player;
        this.region = region;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MinigamePlayer getMinigamePlayer() {
        return player;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
