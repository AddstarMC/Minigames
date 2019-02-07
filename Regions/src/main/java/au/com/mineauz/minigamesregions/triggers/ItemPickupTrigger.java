package au.com.mineauz.minigamesregions.triggers;

public class ItemPickupTrigger implements Trigger {

    @Override
    public String getName() {
        return "ITEM_PICKUP";
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
