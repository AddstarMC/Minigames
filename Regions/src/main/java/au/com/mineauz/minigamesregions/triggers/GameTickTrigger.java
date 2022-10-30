package au.com.mineauz.minigamesregions.triggers;

public class GameTickTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_TICK";
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return false;
    }

    @Override
    public boolean triggerOnPlayerAvailable() {
        return false;
    }
}
