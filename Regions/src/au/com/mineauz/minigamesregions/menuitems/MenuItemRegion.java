package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.RegionModule;

public class MenuItemRegion extends MenuItem{
	
	private Region region;
	private RegionModule rmod;

	public MenuItemRegion(String name, Material displayItem, Region region, RegionModule rmod) {
		super(name, displayItem);
		this.region = region;
		this.rmod = rmod;
	}

	public MenuItemRegion(String name, String description, Material displayItem, Region region, RegionModule rmod) {
		super(name, description, displayItem);
		this.region = region;
		this.rmod = rmod;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = createMenu(player, getContainer(), region);
		m.displayMenu(player);
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		rmod.removeRegion(region.getName());
		remove();
	}
	
	public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Region region){
		Menu m = new Menu(3, "Region: " + region.getName());
		List<MenuItem> items = new ArrayList<MenuItem>();
		for(TriggerExecutor ex : region.getExecutors()){
			items.add(new MenuItemExecutor(region, ex));
		}

		m.setControlItem(new MenuItemExecutorAdd("Add Executor", Material.ITEM_FRAME, region), 4);
		m.addItems(items);
		
		return m;
	}

}
