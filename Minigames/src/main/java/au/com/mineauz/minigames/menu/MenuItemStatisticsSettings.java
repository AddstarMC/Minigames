package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuItemStatisticsSettings extends MenuItem {
    private final @NotNull Minigame minigame;

    public MenuItemStatisticsSettings(@Nullable Material displayMat, @Nullable Component name, @NotNull Minigame minigame) {
        super(displayMat, name);
        this.minigame = minigame;
    }

    @Override
    public ItemStack onClick() {
        Menu subMenu = new Menu(6, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_STAT_SETTINGS_NAME),
                getContainer().getViewer());

        for (MinigameStat stat : MinigameStats.getAllStats().values()) {
            subMenu.addItem(new MenuItemModifyStatSetting(Material.WRITABLE_BOOK, minigame, stat));
        }

        subMenu.addItem(new MenuItemBack(getContainer()), subMenu.getSize() - 9);
        subMenu.displayMenu(getContainer().getViewer());

        return super.onClick();
    }
}
