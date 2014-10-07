package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class MenuItemNodeExecutorAdd extends MenuItem{
	
	private Node node;

	public MenuItemNodeExecutorAdd(String name, Material displayItem, Node node) {
		super(name, displayItem);
		this.node = node;
	}

	public MenuItemNodeExecutorAdd(String name, List<String> description, Material displayItem, Node node) {
		super(name, description, displayItem);
		this.node = node;
	}
	
	@Override
	public ItemStack onClick(){
		Menu m = new Menu(6, "Select Trigger", getContainer().getViewer());
		
		List<String> triggers = new ArrayList<String>(Triggers.getAllNodeTriggers());
		Collections.sort(triggers);
		
		for(String trig : triggers){
			m.addItem(new MenuItemTrigger(Triggers.getTrigger(trig), node, getContainer()));
		}
		
		m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
		
		m.displayMenu(getContainer().getViewer());
		
		return null;
	}
}
