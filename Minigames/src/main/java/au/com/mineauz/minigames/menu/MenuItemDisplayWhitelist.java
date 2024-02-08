package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayWhitelist extends MenuItem {
    private final @NotNull List<@NotNull Material> whitelist;
    private final @NotNull Callback<Boolean> whitelistMode;
    private final @NotNull List<@NotNull Component> modeDescription;

    public MenuItemDisplayWhitelist(@Nullable Material displayMat, @Nullable Component name, @NotNull List<@NotNull Material> whitelist,
                                    @NotNull Callback<Boolean> whitelistMode, @NotNull List<@NotNull Component> modeDescription) {
        this(displayMat, name, null, whitelist, whitelistMode, modeDescription);
    }

    public MenuItemDisplayWhitelist(@Nullable Material displayMat, @Nullable Component name,
                                    @Nullable List<@NotNull Component> mainDescription, @NotNull List<@NotNull Material> whitelist,
                                    @NotNull Callback<Boolean> whitelistMode, @NotNull List<@NotNull Component> modeDescription) {
        super(displayMat, name, mainDescription);
        this.whitelist = whitelist;
        this.whitelistMode = whitelistMode;
        this.modeDescription = modeDescription;
    }

    @Override
    public ItemStack onClick() {
        Menu menu = new Menu(6, MgMenuLangKey.MENU_WHITELIST_BLOCK_NAME, getContainer().getViewer());
        List<MenuItem> items = new ArrayList<>();
        for (Material bl : whitelist) {
            items.add(new MenuItemWhitelistBlock(bl, whitelist));
        }
        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.addItem(new MenuItemAddWhitelistBlock(MgMenuLangKey.MENU_WHITELIST_ADDMATERIAL_NAME, whitelist), menu.getSize() - 1);
        menu.addItem(new MenuItemBoolean(Material.ENDER_PEARL, MgMenuLangKey.MENU_WHITELIST_MODE, modeDescription,
                whitelistMode), menu.getSize() - 2);
        menu.addItems(items);
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
