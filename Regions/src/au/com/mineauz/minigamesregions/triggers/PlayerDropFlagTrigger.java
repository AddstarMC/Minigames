package au.com.mineauz.minigamesregions.triggers;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 13/02/2017.
 */
public class PlayerDropFlagTrigger implements Trigger {
    @Override
    public String getName() {
        return "PLAYER_DROP_FLAG";
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }
}
