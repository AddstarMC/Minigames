package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemPage extends MenuItem {
    private final @NotNull Menu menu;

    public MenuItemPage(@Nullable Material displayMat, @Nullable Component name, @NotNull Menu menu) {
        super(displayMat, name);
        this.menu = menu;
    }

    public MenuItemPage(@Nullable Material displayMat, @NotNull LangKey name, @NotNull Menu menu) {
        super(displayMat, name);
        this.menu = menu;
    }

    public MenuItemPage(@Nullable Material displayMat, @Nullable LangKey langKey,
                        @Nullable List<@NotNull Component> description, @NotNull Menu menu) {
        super(displayMat, langKey, description);
        this.menu = menu;
    }

    public MenuItemPage(@Nullable Material displayMat, @Nullable Component name,
                        @Nullable List<@NotNull Component> description, @NotNull Menu menu) {
        super(displayMat, name, description);
        this.menu = menu;
    }

    @Override
    public ItemStack onClick() {
        menu.setPreviousPage(getContainer());
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
