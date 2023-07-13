package au.com.mineauz.minigamesregions.triggers;

public class StopGlideTrigger implements Trigger {

    @Override
    public String getName() {
        return "STOP_GLIDE";
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
