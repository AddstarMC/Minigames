package au.com.mineauz.minigamesregions.triggers;

public class RightClickBlockTrigger implements Trigger {

    @Override
    public String getName() {
        return "RIGHT_CLICK_BLOCK";
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
