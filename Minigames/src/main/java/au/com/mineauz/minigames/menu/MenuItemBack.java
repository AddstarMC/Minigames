package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuItemBack extends MenuItem {
    private final @NotNull Menu prev;

    public MenuItemBack(@NotNull Menu prev) {
        super(MenuUtility.getBackMaterial(), MgMenuLangKey.MENU_PAGE_BACK);
        this.prev = prev;
    }

    @Override
    public ItemStack onClick() {
        prev.displayMenu(prev.getViewer());
        return null;
    }
}
