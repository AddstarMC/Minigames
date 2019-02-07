package au.com.mineauz.minigamesregions.triggers;

public class PlayerXPChangeTrigger implements Trigger {
    @Override
    public String getName() {
        return "XP_CHANGE";
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
