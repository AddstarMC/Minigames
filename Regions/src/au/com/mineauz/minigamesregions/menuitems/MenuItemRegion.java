package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;
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
	public ItemStack onClick(MinigamePlayer player){
		Menu m = createMenu(player, getContainer(), region);
		m.displayMenu(player);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		rmod.removeRegion(region.getName());
		getContainer().removeItem(getSlot());
		return null;
	}
	
	public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Region region){
		Menu m = new Menu(3, "Region: " + region.getName());
		m.setPreviousPage(previousPage);
		List<MenuItem> items = new ArrayList<MenuItem>();
		for(RegionExecutor ex : region.getExecutors()){
			items.add(new MenuItemRegionExecutor(region, ex));
		}
		if(previousPage != null){
			m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previousPage), m.getSize() - 9);
		}
		m.addItem(new MenuItemRegionExecutorAdd("Add Executor", Material.ITEM_FRAME, region), m.getSize() - 1);
		m.addItems(items);
		
		return m;
	}

}
