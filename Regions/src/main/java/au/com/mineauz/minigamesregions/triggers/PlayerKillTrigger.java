package au.com.mineauz.minigamesregions.triggers;

/**
 * Created for the AddstarMC
 * Created by Narimm on 10/10/2017.
 */
public class PlayerKillTrigger implements Trigger {
    @Override
    public String getName() {
        return "PLAYER_KILL";
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
