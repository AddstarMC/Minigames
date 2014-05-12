package com.pauldavdesign.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigamesregions.NodeExecutor;
import com.pauldavdesign.mineauz.minigamesregions.RegionExecutor;
import com.pauldavdesign.mineauz.minigamesregions.actions.ActionInterface;

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
	public ItemStack onClick(){
		if(rexec != null){
			if(act.displayMenu(getContainer().getViewer(), rexec.getArguments(), getContainer()))
				return null;
		}
		else{
			if(act.displayMenu(getContainer().getViewer(), nexec.getArguments(), getContainer()))
				return null;
		}
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		if(rexec != null)
			rexec.removeAction(act);
		else
			nexec.removeAction(act);
		getContainer().removeItem(getSlot());
		return null;
	}
}
