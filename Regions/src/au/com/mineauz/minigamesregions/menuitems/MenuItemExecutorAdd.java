package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.TriggerArea;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class MenuItemExecutorAdd extends MenuItem{
	private final TriggerArea area;

	public MenuItemExecutorAdd(String name, Material displayItem, TriggerArea area) {
		super(name, displayItem);
		this.area = area;
	}

	public MenuItemExecutorAdd(String name, String description, Material displayItem, TriggerArea area) {
		super(name, description, displayItem);
		this.area = area;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(6, "Select Trigger");
		
		List<String> triggers = new ArrayList<String>(Triggers.getAllRegionTriggers());
		Collections.sort(triggers);
		
		for(String trig : triggers){
			m.addItem(new MenuItemTrigger(Triggers.getTrigger(trig), area, getContainer()));
		}
		
		m.displayMenu(player);
	}
}
