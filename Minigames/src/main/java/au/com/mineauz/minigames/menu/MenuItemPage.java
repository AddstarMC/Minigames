package au.com.mineauz.minigames.menu;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemPage extends MenuItem {
    private final @NotNull Menu menu;

    public MenuItemPage(String name, Material displayItem, @NotNull Menu menu) {
        super(name, displayItem);
        Preconditions.checkNotNull(menu, "menu");
        this.menu = menu;
    }

    public MenuItemPage(String name, List<String> description, Material displayItem, @NotNull Menu menu) {
        super(name, description, displayItem);
        Preconditions.checkNotNull(menu, "menu");
        this.menu = menu;
    }

    @Override
    public ItemStack onClick() {
        menu.setPreviousPage(getContainer());
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
