package au.com.mineauz.minigamesregions.conditions;

import org.jetbrains.annotations.NotNull;

public interface ConditionFactory {
    @NotNull ACondition makeNewCondition();

    @NotNull String getName();
}
