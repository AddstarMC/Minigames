package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class MenuItemRegionExecutorAdd extends MenuItem{
	
	private Region region;

	public MenuItemRegionExecutorAdd(String name, Material displayItem, Region region) {
		super(name, displayItem);
		this.region = region;
	}

	public MenuItemRegionExecutorAdd(String name, String description, Material displayItem, Region region) {
		super(name, description, displayItem);
		this.region = region;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(6, "Select Trigger");
		
		List<String> triggers = new ArrayList<String>(Triggers.getAllRegionTriggers());
		Collections.sort(triggers);
		
		for(String trig : triggers){
			m.addItem(new MenuItemTrigger(Triggers.getTrigger(trig), region, getContainer()));
		}
		
		m.displayMenu(player);
	}
}
