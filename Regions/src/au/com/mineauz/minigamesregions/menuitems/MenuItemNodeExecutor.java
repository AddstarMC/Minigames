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
import au.com.mineauz.minigamesregions.NodeTrigger;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;

public class MenuItemNodeExecutor extends MenuItem{
	
	private Node node;

	public MenuItemNodeExecutor(String name, Material displayItem, Node node) {
		super(name, displayItem);
		this.node = node;
	}

	public MenuItemNodeExecutor(String name, List<String> description, Material displayItem, Node node) {
		super(name, description, displayItem);
		this.node = node;
	}
	
	@Override
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the name of a trigger to create a new executor. "
				+ "Window will reopen in 60s if nothing is entered.", null);
		List<String> triggers = new ArrayList<String>(NodeTrigger.values().length);
		for(NodeTrigger t : NodeTrigger.values()){
			triggers.add(MinigameUtils.capitalize(t.toString()));
		}
		List<String> actions = new ArrayList<String>(Actions.getAllActionNames().size());
		for(String a : Actions.getAllActionNames()){
			actions.add(MinigameUtils.capitalize(a));
		}
		ply.sendMessage("Triggers: " + MinigameUtils.listToString(triggers));
		ply.setManualEntry(this);

		getContainer().startReopenTimer(60);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(NodeTrigger.getByName(entry) != null){
			NodeTrigger trig = NodeTrigger.getByName(entry);
			
			final NodeExecutor ex = new NodeExecutor(trig);
			node.addExecutor(ex);
			List<String> des = MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getTrigger().toString()) + ";" +
					ChatColor.GREEN + "Actions: " + ChatColor.GRAY + 
					ex.getActions().size() + ";" + 
					ChatColor.DARK_PURPLE + "(Right click to delete);" + 
					"(Left click to edit)");
			MenuItemCustom cmi = new MenuItemCustom("Executor ID: " + node.getExecutors().size(), 
					des, Material.ENDER_PEARL);

			cmi.setRightClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					node.removeExecutor(ex);
					getContainer().removeItem(getSlot());
					return null;
				}
			});
			final MinigamePlayer fviewer = getContainer().getViewer();
			final Menu fm = getContainer();
			cmi.setClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					Menu m = new Menu(3, "Executor", fviewer);
					final Menu ffm = m;
					MenuItemCustom ca = new MenuItemCustom("Actions", Material.CHEST);
					ca.setClick(new InteractionInterface() {
						
						@Override
						public Object interact(Object object) {
							Actions.displayMenu(fviewer, ex, ffm);
							return null;
						}
					});
					m.addItem(ca);
					MenuItemCustom c2 = new MenuItemCustom("Conditions", Material.CHEST);
					c2.setClick(new InteractionInterface() {
						
						@Override
						public Object interact(Object object) {
							Conditions.displayMenu(fviewer, ex, ffm);
							return null;
						}
					});
					m.addItem(c2);
					m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, fm), m.getSize() - 9);
					m.displayMenu(fviewer);
					return null;
				}
			});
			getContainer().addItem(cmi);
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid trigger type!", "error");
	}
}
