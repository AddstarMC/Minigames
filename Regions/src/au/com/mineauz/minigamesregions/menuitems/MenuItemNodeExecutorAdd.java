package au.com.mineauz.minigamesregions.menuitems;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;
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
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the name of a trigger to create a new executor. "
				+ "Window will reopen in 60s if nothing is entered.", null);
		ply.sendMessage("Triggers: " + MinigameUtils.listToString(Triggers.getAllNodeTriggers()));
		ply.setManualEntry(this);

		getContainer().startReopenTimer(60);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		if(Triggers.getAllNodeTriggers().contains(entry.toUpperCase())){
			Trigger trig = Triggers.getTrigger(entry.toUpperCase());
			
			final NodeExecutor ex = new NodeExecutor(trig);
			node.addExecutor(ex);
			
			getContainer().addItem(new MenuItemNodeExecutor(node, ex));
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid trigger type!", "error");
	}
}
