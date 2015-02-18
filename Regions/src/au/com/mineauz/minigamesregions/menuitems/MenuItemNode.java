package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionModule;

public class MenuItemNode extends MenuItem{
	
	private Node node;
	private RegionModule rmod;

	public MenuItemNode(String name, Material displayItem, Node node, RegionModule rmod) {
		super(name, displayItem);
		this.node = node;
		this.rmod = rmod;
	}

	public MenuItemNode(String name, List<String> description, Material displayItem, Node node, RegionModule rmod) {
		super(name, description, displayItem);
		this.node = node;
		this.rmod = rmod;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		Menu m = createMenu(player, getContainer(), node);
		m.displayMenu(player);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		rmod.removeNode(node.getName());
		remove();
		return null;
	}
	
	public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Node node){
		Menu m = new Menu(3, "Node: " + node.getName());
		List<MenuItem> items = new ArrayList<MenuItem>();
		for(NodeExecutor ex : node.getExecutors()){
			items.add(new MenuItemNodeExecutor(node, ex));
		}
		m.setControlItem(new MenuItemNodeExecutorAdd("Add Executor", Material.ITEM_FRAME, node), 4);
		m.addItems(items);
		
		return m;
	}

}
