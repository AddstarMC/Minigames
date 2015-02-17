package au.com.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.actions.ActionInterface;

public class MenuItemAction extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;
	private ActionInterface act;

	public MenuItemAction(String name, Material displayItem, RegionExecutor exec, ActionInterface act) {
		super(name, displayItem);
		this.rexec = exec;
		this.act = act;
	}
	
	public MenuItemAction(String name, Material displayItem, NodeExecutor exec, ActionInterface act) {
		super(name, displayItem);
		this.nexec = exec;
		this.act = act;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		if(rexec != null){
			if(act.displayMenu(player, getContainer()))
				return null;
		}
		else{
			if(act.displayMenu(player, getContainer()))
				return null;
		}
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		if(rexec != null)
			rexec.removeAction(act);
		else
			nexec.removeAction(act);
		getContainer().removeItem(getSlot());
		return null;
	}
}
