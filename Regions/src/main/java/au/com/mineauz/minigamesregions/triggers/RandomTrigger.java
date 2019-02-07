package au.com.mineauz.minigamesregions.triggers;

public class RandomTrigger implements Trigger {

    @Override
    public String getName() {
        return "RANDOM";
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
