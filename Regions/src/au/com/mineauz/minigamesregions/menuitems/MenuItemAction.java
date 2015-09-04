package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.actions.ActionInterface;

public class MenuItemAction extends MenuItem{
	
	private TriggerExecutor executor;
	private ActionInterface act;

	public MenuItemAction(String name, Material displayItem, TriggerExecutor exec, ActionInterface act) {
		super(name, displayItem);
		this.executor = exec;
		this.act = act;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		act.displayMenu(player, getContainer());
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		executor.getActions().remove(act);
		remove();
	}
}
