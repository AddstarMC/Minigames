package au.com.mineauz.minigamesregions.triggers;

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

    @Override
    public boolean triggerOnPlayerAvailable() {
        return true;
    }
}
