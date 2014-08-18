package au.com.mineauz.minigamesregions.menuitems;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class MenuItemRegionExecutorAdd extends MenuItem{
	
	private Region region;

	public MenuItemRegionExecutorAdd(String name, Material displayItem, Region region) {
		super(name, displayItem);
		this.region = region;
	}

	public MenuItemRegionExecutorAdd(String name, List<String> description, Material displayItem, Region region) {
		super(name, description, displayItem);
		this.region = region;
	}
	
	@Override
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the name of a trigger to create a new executor. "
				+ "Window will reopen in 60s if nothing is entered.", null);
		ply.sendMessage("Triggers: " + MinigameUtils.listToString(Triggers.getAllRegionTriggers()));
		ply.setManualEntry(this);

		getContainer().startReopenTimer(60);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(Triggers.getAllRegionTriggers().contains(entry.toUpperCase())){
			Trigger trig = Triggers.getTrigger(entry.toUpperCase());
			
			final RegionExecutor ex = new RegionExecutor(trig);
			region.addExecutor(ex);
			
			getContainer().addItem(new MenuItemRegionExecutor(region, ex));
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid trigger type!", "error");
	}
}
