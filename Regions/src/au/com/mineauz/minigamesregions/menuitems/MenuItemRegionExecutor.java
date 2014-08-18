package au.com.mineauz.minigamesregions.menuitems;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class MenuItemRegionExecutor extends MenuItem{
	
	private Region region;

	public MenuItemRegionExecutor(String name, Material displayItem, Region region) {
		super(name, displayItem);
		this.region = region;
	}

	public MenuItemRegionExecutor(String name, List<String> description, Material displayItem, Region region) {
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
			List<String> des = MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getTrigger().toString()) + ";" +
					ChatColor.GREEN + "Actions: " + ChatColor.GRAY + 
					ex.getActions().size() + ";" + 
					ChatColor.DARK_PURPLE + "(Right click to delete);" + 
					"(Left click to edit)");
			MenuItemCustom cmi = new MenuItemCustom("Executor ID: " + region.getExecutors().size(), 
					des, Material.ENDER_PEARL);

			cmi.setRightClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					region.removeExecutor(ex);
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
					m.addItem(new MenuItemNewLine());
					m.addItem(new MenuItemInteger("Trigger Count", 
							MinigameUtils.stringToList("Number of times this;region can be;triggered"), 
							Material.DOUBLE_STEP, ex.getTriggerCountCallback(), 0, null));
					m.addItem(new MenuItemBoolean("Trigger Per Player", 
							MinigameUtils.stringToList("Whether this region;is triggered per player;or just on count"), 
							Material.ENDER_PEARL, ex.getIsTriggerPerPlayerCallback()));
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
