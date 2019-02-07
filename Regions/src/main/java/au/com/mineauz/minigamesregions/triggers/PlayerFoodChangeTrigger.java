package au.com.mineauz.minigamesregions.triggers;

public class PlayerFoodChangeTrigger implements Trigger {
    @Override
    public String getName() {
        return "FOOD_CHANGE";
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
