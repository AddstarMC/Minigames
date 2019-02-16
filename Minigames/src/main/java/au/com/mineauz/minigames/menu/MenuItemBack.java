package au.com.mineauz.minigames.menu;

import org.bukkit.inventory.ItemStack;

public class MenuItemBack extends MenuItem {

    private Menu prev;

    public MenuItemBack(Menu prev) {
        super("Back", MenuUtility.getBackMaterial());
        this.prev = prev;
    }

    @Override
    public ItemStack onClick() {
        prev.displayMenu(prev.getViewer());
        return null;
    }

}
