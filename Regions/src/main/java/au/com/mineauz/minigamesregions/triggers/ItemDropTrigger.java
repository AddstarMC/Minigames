package au.com.mineauz.minigamesregions.triggers;

public class ItemDropTrigger implements Trigger {

    @Override
    public String getName() {
        return "ITEM_DROP";
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
