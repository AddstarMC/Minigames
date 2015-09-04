package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.TriggerArea;
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;

public class MenuItemTrigger extends MenuItem{
	private final Trigger trigger;
	private final TriggerArea area;
	
	private Menu previous;

	public MenuItemTrigger(Trigger trigger, TriggerArea area, Menu previous) {
		super(MinigameUtils.capitalize(trigger.getName().replace("_", " ")), Material.LEVER);
		this.trigger = trigger;
		this.area = area;
		this.previous = previous;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		TriggerExecutor executor = area.addExecutor(trigger);
		previous.addItem(new MenuItemExecutor(area, executor));
		player.showPreviousMenu();
	}

}
