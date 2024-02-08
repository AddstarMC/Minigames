package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigamesregions.RegionModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemRegenRegion extends MenuItem {
    private final @NotNull MgRegion region;
    private final @NotNull RegionModule rmod;

    public MenuItemRegenRegion(@Nullable Material displayMat, @Nullable Component name, @NotNull MgRegion region,
                               @NotNull RegionModule rmod) {
        super(displayMat, name);
        this.region = region;
        this.rmod = rmod;
    }

    public MenuItemRegenRegion(@Nullable Material displayMat, @Nullable Component name,
                               @Nullable List<@NotNull Component> description, @NotNull MgRegion region,
                               @NotNull RegionModule rmod) {
        super(displayMat, name, description);
        this.region = region;
        this.rmod = rmod;
    }

    //there is nothing in need of configuration
    @Override
    public ItemStack onClick() {
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        rmod.getMinigame().removeRegenRegion(region.getName());
        getContainer().removeItem(getSlot());
        return null;
    }
}
