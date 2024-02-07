package au.com.mineauz.minigamesregions.triggers;

public class GameEndedTrigger implements Trigger {
    //todo deleted
    @Override
    public String getName() {
        return "GAME_ENDED";
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public boolean triggerOnPlayerAvailable() {
        return true;
    }
}
