package au.com.mineauz.minigamesregions.triggers;

public class MoveInRegionTrigger implements Trigger {

    @Override
    public String getName() {
        return "MOVE_IN_REGION";
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return false;
    }

    @Override
    public boolean triggerOnPlayerAvailable() {
        return true;
    }
}
