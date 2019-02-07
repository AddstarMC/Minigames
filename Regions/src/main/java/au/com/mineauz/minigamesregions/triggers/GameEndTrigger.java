package au.com.mineauz.minigamesregions.triggers;

public class GameEndTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_END";
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

}
