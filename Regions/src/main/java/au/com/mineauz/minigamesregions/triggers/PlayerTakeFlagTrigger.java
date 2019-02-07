package au.com.mineauz.minigamesregions.triggers;

public class PlayerTakeFlagTrigger implements Trigger {
    @Override
    public String getName() {
        return "PLAYER_TAKE_FLAG";
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
