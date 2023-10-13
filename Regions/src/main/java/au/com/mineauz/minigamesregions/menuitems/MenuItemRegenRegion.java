package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigamesregions.RegionModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemRegenRegion extends MenuItem {
    private final MgRegion region;
    private final RegionModule rmod;

    public MenuItemRegenRegion(String name, Material displayItem, MgRegion region, RegionModule rmod) {
        super(name, displayItem);
        this.region = region;
        this.rmod = rmod;
    }

    public MenuItemRegenRegion(String name, List<String> description, Material displayItem, MgRegion region, RegionModule rmod) {
        super(name, description, displayItem);
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
