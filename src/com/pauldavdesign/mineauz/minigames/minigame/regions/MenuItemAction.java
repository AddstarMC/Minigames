package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.minigame.regions.actions.RegionActionInterface;

public class MenuItemAction extends MenuItem{
	
	private RegionExecutor exec;
	private RegionActionInterface act;

	public MenuItemAction(String name, Material displayItem, RegionExecutor exec, RegionActionInterface act) {
		super(name, displayItem);
		this.exec = exec;
		this.act = act;
	}
	
	@Override
	public ItemStack onClick(){
		if(act.displayMenu(getContainer().getViewer(), exec.getArguments(), getContainer()))
			return null;
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		exec.removeAction(act);;
		getContainer().removeItem(getSlot());
		return null;
	}
}
