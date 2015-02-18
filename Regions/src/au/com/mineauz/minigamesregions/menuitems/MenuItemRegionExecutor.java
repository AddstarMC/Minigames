package au.com.mineauz.minigamesregions.menuitems;

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
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;

public class MenuItemRegionExecutor extends MenuItem{
	
	private Region region;
	private RegionExecutor ex;

	public MenuItemRegionExecutor(Region region, RegionExecutor ex) {
		super("Region Executor:", Material.ENDER_PEARL);
		this.region = region;
		this.ex = ex;
		setDescription(MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getTrigger().getName()) + ";" +
					ChatColor.GREEN + "Actions: " + ChatColor.GRAY + 
					ex.getActions().size() + ";" + 
					ChatColor.DARK_PURPLE + "(Right click to delete);" + 
					"(Left click to edit)"));
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		Menu m = new Menu(3, "Executor");
		final Menu ffm = m;
		
		MenuItemCustom ca = new MenuItemCustom("Actions", Material.CHEST);
		ca.setClick(new InteractionInterface() {
			
			@Override
			public Object interact(MinigamePlayer player, Object object) {
				Actions.displayMenu(player, ex, ffm);
				return null;
			}
		});
		m.addItem(ca);
		
		MenuItemCustom c2 = new MenuItemCustom("Conditions", Material.CHEST);
		c2.setClick(new InteractionInterface() {
			
			@Override
			public Object interact(MinigamePlayer player, Object object) {
				Conditions.displayMenu(player, ex, ffm);
				return null;
			}
		});
		m.addItem(c2);
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemInteger("Trigger Count", 
				MinigameUtils.stringToList("Number of times this;node can be;triggered"), 
				Material.STONE, ex.getTriggerCountCallback(), 0, null));
		
		m.addItem(new MenuItemBoolean("Trigger Per Player", 
				MinigameUtils.stringToList("Whether this node;is triggered per player;or just on count"), 
				Material.ENDER_PEARL, ex.getIsTriggerPerPlayerCallback()));
		m.displayMenu(player);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		region.removeExecutor(ex);
		remove();
		return null;
	}

}
