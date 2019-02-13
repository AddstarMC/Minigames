package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.RegionModule;

public class MenuItemRegion extends MenuItem{
    
    private Region region;
    private RegionModule rmod;

    public MenuItemRegion(String name, Material displayItem, Region region, RegionModule rmod) {
        super(name, displayItem);
        this.region = region;
        this.rmod = rmod;
    }

    public MenuItemRegion(String name, List<String> description, Material displayItem, Region region, RegionModule rmod) {
        super(name, description, displayItem);
        this.region = region;
        this.rmod = rmod;
    }
    
    @Override
    public ItemStack onClick(){
        Menu m = createMenu(getContainer().getViewer(), getContainer(), region);
        m.displayMenu(getContainer().getViewer());
        return null;
    }
    
    @Override
    public ItemStack onRightClick(){
        rmod.removeRegion(region.getName());
        getContainer().removeItem(getSlot());
        return null;
    }
    
    public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Region region){
        Menu m = new Menu(3, "Region: " + region.getName(), viewer);
        m.setPreviousPage(previousPage);
        List<MenuItem> items = new ArrayList<>();
        for(RegionExecutor ex : region.getExecutors()){
            items.add(new MenuItemRegionExecutor(region, ex));
        }
        if(previousPage != null){
            m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previousPage), m.getSize() - 9);
        }
        m.addItem(new MenuItemRegionExecutorAdd("Add Executor", MenuUtility.getCreateMaterial(), region), m.getSize() - 1);
        m.addItems(items);
        
        return m;
    }

}
