package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditionInterface;

public class MenuItemCondition extends MenuItem{
	
	private RegionExecutor exec;
	private RegionConditionInterface con;

	public MenuItemCondition(String name, Material displayItem, RegionExecutor exec, RegionConditionInterface con) {
		super(name, displayItem);
		this.exec = exec;
		this.con = con;
	}
	
	@Override
	public ItemStack onClick(){
		if(con.displayMenu(getContainer().getViewer(), getContainer(), exec))
			return null;
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		exec.removeCondition(con);
		getContainer().removeItem(getSlot());
		return null;
	}

}
