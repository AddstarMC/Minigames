package au.com.mineauz.minigamesregions.triggers;

public interface Trigger {

    String getName();

    boolean useInRegions();

    boolean useInNodes();

    boolean triggerOnPlayerAvailable();
}
