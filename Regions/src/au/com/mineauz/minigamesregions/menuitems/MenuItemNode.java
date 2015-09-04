package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.TriggerExecutor;

public class MenuItemNode extends MenuItem{
	
	private Node node;
	private RegionModule rmod;

	public MenuItemNode(String name, Material displayItem, Node node, RegionModule rmod) {
		super(name, displayItem);
		this.node = node;
		this.rmod = rmod;
	}

	public MenuItemNode(String name, String description, Material displayItem, Node node, RegionModule rmod) {
		super(name, description, displayItem);
		this.node = node;
		this.rmod = rmod;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = createMenu(player, getContainer(), node);
		m.displayMenu(player);
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		rmod.removeNode(node.getName());
		remove();
	}
	
	public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Node node){
		Menu m = new Menu(3, "Node: " + node.getName());
		List<MenuItem> items = new ArrayList<MenuItem>();
		for(TriggerExecutor ex : node.getExecutors()){
			items.add(new MenuItemExecutor(node, ex));
		}
		m.setControlItem(new MenuItemExecutorAdd("Add Executor", Material.ITEM_FRAME, node), 4);
		m.addItems(items);
		
		return m;
	}

}
