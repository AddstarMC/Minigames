package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;

public class MenuItemCondition extends MenuItem{
	
	private TriggerExecutor rexec;
	private ConditionInterface con;

	public MenuItemCondition(String name, Material displayItem, TriggerExecutor exec, ConditionInterface con) {
		super(name, displayItem);
		this.rexec = exec;
		this.con = con;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		con.displayMenu(player, getContainer());
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		rexec.getConditions().remove(con);
		remove();
	}

}
