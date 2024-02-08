package au.com.mineauz.minigamesregions.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConditionCategoryRegistry {
    private final static @NotNull Set<IConditionCategory> conditionCategories = new LinkedHashSet<>();

    static {
        for (IConditionCategory category : RegionConditionCategories.values()) {
            registerCategory(category);
        }
    }

    public static void registerCategory(@NotNull IConditionCategory category) {
        conditionCategories.add(category);
    }

    public List<IConditionCategory> getCategories() {
        return new ArrayList<>(conditionCategories);
    }
}
