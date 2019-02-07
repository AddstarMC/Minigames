package au.com.mineauz.minigamesregions.triggers;

public class InteractTrigger implements Trigger {

    @Override
    public String getName() {
        return "INTERACT";
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

}
