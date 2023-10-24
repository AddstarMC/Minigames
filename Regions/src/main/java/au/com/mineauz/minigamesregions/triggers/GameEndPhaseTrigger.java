package au.com.mineauz.minigamesregions.triggers;

public class GameEndPhaseTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_ENDPHASE";
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
