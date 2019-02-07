package au.com.mineauz.minigamesregions.triggers;

public class BlockPlaceTrigger implements Trigger {

    @Override
    public String getName() {
        return "BLOCK_PLACE";
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
