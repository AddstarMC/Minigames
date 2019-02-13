package au.com.mineauz.minigamesregions.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;

public class LeaveRegionEvent extends Event{

private static final HandlerList handlers = new HandlerList();
    
    private MinigamePlayer player;
    private Region region;
    
    public LeaveRegionEvent(MinigamePlayer player, Region region){
        this.player = player;
        this.region = region;
    }
    
    public MinigamePlayer getMinigamePlayer(){
        return player;
    }
    
    public Region getRegion(){
        return region;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
