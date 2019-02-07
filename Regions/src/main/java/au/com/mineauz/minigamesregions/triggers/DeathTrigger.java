package au.com.mineauz.minigamesregions.triggers;

public class DeathTrigger implements Trigger {

    @Override
    public String getName() {
        return "DEATH";
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
