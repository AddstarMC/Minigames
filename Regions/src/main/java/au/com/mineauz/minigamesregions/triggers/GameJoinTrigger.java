package au.com.mineauz.minigamesregions.triggers;

public class GameJoinTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_JOIN";
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
