package au.com.mineauz.minigamesregions.triggers;

public class RemoteTrigger implements Trigger {

    @Override
    public String getName() {
        return "REMOTE";
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
