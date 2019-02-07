package au.com.mineauz.minigamesregions.triggers;

public class BlockBreakTrigger implements Trigger {

    @Override
    public String getName() {
        return "BLOCK_BREAK";
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
