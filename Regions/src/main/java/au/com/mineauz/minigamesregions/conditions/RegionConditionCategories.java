package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum RegionConditionCategories implements IConditionCategory {
    WORLD(RegionLangKey.MENU_CONDITIONCATEGORY_WORLD_NAME),
    TEAM(RegionLangKey.MENU_CONDITIONCATEGORY_TEAM_NAME),
    PLAYER(RegionLangKey.MENU_CONDITIONCATEGORY_PLAYER_NAME),
    MINIGAME(RegionLangKey.MENU_CONDITIONCATEGORY_MINIGAME_NAME),
    MISC(RegionLangKey.MENU_CONDITIONCATEGORY_MISC_NAME);

    private final @NotNull RegionLangKey langKey;

    RegionConditionCategories(@NotNull RegionLangKey langKey) {
        this.langKey = langKey;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(langKey);
    }
}
