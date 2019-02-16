package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;

public class MenuItemStatisticsSettings extends MenuItem {
    private final Minigame minigame;

    public MenuItemStatisticsSettings(Minigame minigame, String name, Material displayItem) {
        super(name, displayItem);
        this.minigame = minigame;
    }

    @Override
    public ItemStack onClick() {
        Menu subMenu = new Menu(6, "Statistics Settings", getContainer().getViewer());

        for (MinigameStat stat : MinigameStats.getAllStats().values()) {
            subMenu.addItem(new MenuItemModifyStatSetting(minigame, stat, Material.WRITABLE_BOOK));
        }

        subMenu.addItem(new MenuItemBack(getContainer()), subMenu.getSize() - 9);
        subMenu.displayMenu(getContainer().getViewer());

        return super.onClick();
    }
}
