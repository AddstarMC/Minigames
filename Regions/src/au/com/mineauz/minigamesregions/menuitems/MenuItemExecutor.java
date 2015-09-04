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
import au.com.mineauz.minigamesregions.TriggerArea;
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;

public class MenuItemExecutor extends MenuItem{
	private final TriggerArea area;
	private final TriggerExecutor executor;

	public MenuItemExecutor(TriggerArea area, TriggerExecutor executor) {
		super("Region Executor:", Material.ENDER_PEARL);
		
		this.area = area;
		this.executor = executor;
		
		setDescription(MinigameUtils.stringToList(
				ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + MinigameUtils.capitalize(executor.getTrigger().getName()) + ";" +
				ChatColor.GREEN + "Actions: " + ChatColor.GRAY + executor.getActions().size() + ";" + 
				ChatColor.DARK_PURPLE + "(Right click to delete);" + 
				"(Left click to edit)")
				);
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(3, "Executor");
		final Menu ffm = m;
		
		MenuItem ca = new MenuItem("Actions", Material.CHEST);
		ca.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Actions.displayMenu(player, executor, ffm);
			}
		});
		m.addItem(ca);
		
		MenuItem c2 = new MenuItem("Conditions", Material.CHEST);
		c2.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Conditions.displayMenu(player, executor, ffm);
			}
		});
		m.addItem(c2);
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemInteger("Trigger Count", 
				"Number of times this;node can be;triggered", 
				Material.STONE, executor.triggerCount(), 0, Integer.MAX_VALUE));
		
		m.addItem(new MenuItemBoolean("Trigger Per Player", 
				"Whether this node;is triggered per player;or just on count", 
				Material.ENDER_PEARL, executor.triggerPerPlayer()));
		m.displayMenu(player);
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		area.removeExecutor(executor);
		remove();
	}

}
