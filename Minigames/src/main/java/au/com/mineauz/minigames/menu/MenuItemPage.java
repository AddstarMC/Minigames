package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemPage extends MenuItem {
    private final @NotNull Menu menu;

    public MenuItemPage(@NotNull Component name, @Nullable Material displayMat, @NotNull Menu menu) {
        super(name, displayMat);
        this.menu = menu;
    }

    public MenuItemPage(@NotNull MgMenuLangKey name, @Nullable Material displayMat, @NotNull Menu menu) {
        super(name, displayMat);
        this.menu = menu;
    }

    public MenuItemPage(@NotNull Component name, @Nullable List<@NotNull Component> description, @Nullable Material displayMat, @NotNull Menu menu) {
        super(name, description, displayMat);
        this.menu = menu;
    }

    @Override
    public ItemStack onClick() {
        menu.setPreviousPage(getContainer());
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
