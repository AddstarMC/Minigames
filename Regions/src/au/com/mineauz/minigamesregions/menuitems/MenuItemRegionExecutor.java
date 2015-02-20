package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
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
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(3, "Executor");
		final Menu ffm = m;
		
		MenuItem ca = new MenuItem("Actions", Material.CHEST);
		ca.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Actions.displayMenu(player, ex, ffm);
			}
		});
		m.addItem(ca);
		
		MenuItem c2 = new MenuItem("Conditions", Material.CHEST);
		c2.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Conditions.displayMenu(player, ex, ffm);
			}
		});
		m.addItem(c2);
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemInteger("Trigger Count", 
				"Number of times this;node can be;triggered", 
				Material.STONE, ex.getTriggerCountCallback(), 0, Integer.MAX_VALUE));
		
		m.addItem(new MenuItemBoolean("Trigger Per Player", 
				"Whether this node;is triggered per player;or just on count", 
				Material.ENDER_PEARL, ex.getIsTriggerPerPlayerCallback()));
		m.displayMenu(player);
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		region.removeExecutor(ex);
		remove();
	}

}
