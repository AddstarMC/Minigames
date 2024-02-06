package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuItemBack extends MenuItem {
    private final @NotNull Menu prev;

    public MenuItemBack(@NotNull Menu prev) {
        super(MgMenuLangKey.MENU_PAGE_BACK, MenuUtility.getBackMaterial());
        this.prev = prev;
    }

    @Override
    public ItemStack onClick() {
        prev.displayMenu(prev.getViewer());
        return null;
    }

}
