package au.com.mineauz.minigamesregions.triggers;

public class PlayerDamageTrigger implements Trigger {
    @Override
    public String getName() {
        return "PLAYER_DAMAGE";
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
