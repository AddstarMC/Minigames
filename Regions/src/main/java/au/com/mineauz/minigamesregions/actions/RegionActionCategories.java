package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum RegionActionCategories implements IActionCategory {
    MINIGAME(RegionLangKey.MENU_ACTIONCATEGORY_MINIGAME_NAME),
    TEAM(RegionLangKey.MENU_ACTIONCATEGORY_TEAM_NAME),
    PLAYER(RegionLangKey.MENU_ACTIONCATEGORY_PLAYER_NAME),
    WORLD(RegionLangKey.MENU_ACTIONCATEGORY_WORLD_NAME),
    SERVER(RegionLangKey.MENU_ACTIONCATEGORY_SERVER_NAME),
    BLOCK(RegionLangKey.MENU_ACTIONCATEGORY_BLOCK_NAME),
    REGION_NODE(RegionLangKey.MENU_ACTIONCATEGORY_REGIONNODE_NAME),
    REMOTE(RegionLangKey.MENU_ACTIONCATEGORY_REMOTE_NAME);

    private final @NotNull RegionLangKey langKey;

    RegionActionCategories(@NotNull RegionLangKey langKey) {
        this.langKey = langKey;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(langKey);
    }
}
