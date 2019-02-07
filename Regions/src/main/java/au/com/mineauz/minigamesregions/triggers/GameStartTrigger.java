package au.com.mineauz.minigamesregions.triggers;

public class GameStartTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_START";
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
