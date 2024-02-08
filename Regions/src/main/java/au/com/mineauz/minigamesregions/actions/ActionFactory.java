package au.com.mineauz.minigamesregions.actions;

import org.jetbrains.annotations.NotNull;

public interface ActionFactory {
    @NotNull ActionInterface makeNewAction();

    @NotNull String getName();
}
