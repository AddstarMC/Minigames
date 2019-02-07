package au.com.mineauz.minigamesregions.triggers;

public class LeaveTrigger implements Trigger {

    @Override
    public String getName() {
        return "LEAVE";
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return false;
    }

}
