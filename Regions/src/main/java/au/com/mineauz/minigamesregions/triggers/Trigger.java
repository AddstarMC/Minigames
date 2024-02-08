package au.com.mineauz.minigamesregions.triggers;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Trigger {

    @NotNull String getName();

    @NotNull Component getDisplayName();

    boolean useInRegions();

    boolean useInNodes();

    boolean triggerOnPlayerAvailable();
}
