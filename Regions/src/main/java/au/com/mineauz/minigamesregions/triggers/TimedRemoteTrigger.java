package au.com.mineauz.minigamesregions.triggers;

/**
 * This action fires if a TimedTriggerAction executes.
 */ //todo deleted
public class TimedRemoteTrigger implements Trigger {

    @Override
    public String getName() {
        return "TIMED_REMOTE";
    }

    @Override
    public boolean useInRegions() {
        return true;
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
