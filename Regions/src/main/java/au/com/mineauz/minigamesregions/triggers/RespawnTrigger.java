package au.com.mineauz.minigamesregions.triggers;

public class RespawnTrigger implements Trigger {

    @Override
    public String getName() {
        return "RESPAWN";
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
