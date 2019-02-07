package au.com.mineauz.minigamesregions.triggers;

public class EnterTrigger implements Trigger {

    @Override
    public String getName() {
        return "ENTER";
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
