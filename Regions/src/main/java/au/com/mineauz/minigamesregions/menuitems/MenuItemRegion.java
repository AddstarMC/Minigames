package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemRegion extends MenuItem {
    private final @NotNull Region region;
    private final @NotNull RegionModule rmod;

    public MenuItemRegion(@Nullable Component name, @Nullable Material displayItem, @NotNull Region region, @NotNull RegionModule rmod) {
        super(name, displayItem);
        this.region = region;
        this.rmod = rmod;
    }

    public MenuItemRegion(@NotNull Component name, @Nullable List<@NotNull Component> description,
                          @Nullable Material displayItem, @NotNull Region region, @Nullable RegionModule rmod) {
        super(name, description, displayItem);
        this.region = region;
        this.rmod = rmod;
    }

    public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Region region) {
        Menu m = new Menu(3, "Region: " + region.getName(), viewer);
        m.setPreviousPage(previousPage);
        List<MenuItem> items = new ArrayList<>();
        for (RegionExecutor ex : region.getExecutors()) {
            items.add(new MenuItemRegionExecutor(region, ex));
        }
        if (previousPage != null) {
            m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previousPage), m.getSize() - 9);
        }
        m.addItem(new MenuItemRegionExecutorAdd("Add Executor", MenuUtility.getCreateMaterial(), region), m.getSize() - 1);
        m.addItems(items);

        return m;
    }

    @Override
    public ItemStack onClick() {
        Menu m = createMenu(getContainer().getViewer(), getContainer(), region);
        m.displayMenu(getContainer().getViewer());
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        rmod.removeRegion(region.getName());
        getContainer().removeItem(getSlot());
        return null;
    }

}
