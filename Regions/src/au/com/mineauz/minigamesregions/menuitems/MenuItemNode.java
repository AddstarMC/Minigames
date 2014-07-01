package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;

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
	public ItemStack onClick(){
		createMenu(getContainer().getViewer(), getContainer(), node);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(){
		rmod.removeNode(node.getName());
		getContainer().removeItem(getSlot());
		return null;
	}
	
	public static void createMenu(MinigamePlayer viewer, Menu previousPage, Node node){
		Menu m = new Menu(3, "Nodes", viewer);
		m.setPreviousPage(previousPage);
		List<MenuItem> items = new ArrayList<MenuItem>();
		int c = 1;
		final Node fnode = node;
		for(NodeExecutor ex : node.getExecutors()){
			List<String> des = MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getTrigger().toString()) + ";" +
					ChatColor.GREEN + "Actions: " + ChatColor.GRAY + 
					ex.getActions().size() + ";" + 
					ChatColor.DARK_PURPLE + "(Right click to delete);(Left clict to edit)");
			MenuItemCustom cmi = new MenuItemCustom("Executor ID: " + c, 
					des, Material.ENDER_PEARL);
			final NodeExecutor cex = ex;
			final MenuItem fcmi = cmi;
			final MinigamePlayer fviewer = viewer;
			final Menu fm = m;
			cmi.setRightClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					fnode.removeExecutor(cex);
					fcmi.getContainer().removeItem(fcmi.getSlot());
					return null;
				}
			});
			cmi.setClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					Menu m = new Menu(3, "Executor", fviewer);
					final Menu ffm = m;
					MenuItemCustom ca = new MenuItemCustom("Actions", Material.CHEST);
					ca.setClick(new InteractionInterface() {
						
						@Override
						public Object interact(Object object) {
							Actions.displayMenu(fviewer, cex, ffm);
							return null;
						}
					});
					m.addItem(ca);
					MenuItemCustom c2 = new MenuItemCustom("Conditions", Material.CHEST);
					c2.setClick(new InteractionInterface() {
						
						@Override
						public Object interact(Object object) {
							Conditions.displayMenu(fviewer, cex, ffm);
							return null;
						}
					});
					m.addItem(c2);
					m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, fm), m.getSize() - 9);
					m.displayMenu(fviewer);
					return null;
				}
			});
			items.add(cmi);
			c++;
		}
		if(previousPage != null){
			m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previousPage), m.getSize() - 9);
		}
		m.addItem(new MenuItemNodeExecutor("Add Executor", Material.ITEM_FRAME, node), m.getSize() - 1);
		m.addItems(items);
		m.displayMenu(viewer);
	}

}
