package au.com.mineauz.minigamesregions.triggers;

public class TickTrigger implements Trigger {

    @Override
    public String getName() {
        return "TICK";
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
