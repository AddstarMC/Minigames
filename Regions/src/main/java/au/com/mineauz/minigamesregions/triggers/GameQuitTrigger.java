package au.com.mineauz.minigamesregions.triggers;

public class GameQuitTrigger implements Trigger {

    @Override
    public String getName() {
        return "GAME_QUIT";
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
