package au.com.mineauz.minigamesregions.triggers;

public class MinigameTimerTrigger implements Trigger{

    @Override
    public String getName() {
        return "MINIGAME_TIMER";
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
