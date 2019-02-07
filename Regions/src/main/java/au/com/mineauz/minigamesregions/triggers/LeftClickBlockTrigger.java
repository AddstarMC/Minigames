package au.com.mineauz.minigamesregions.triggers;

public class LeftClickBlockTrigger implements Trigger {

    @Override
    public String getName() {
        return "LEFT_CLICK_BLOCK";
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
