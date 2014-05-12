package com.pauldavdesign.mineauz.minigamesregions.menuitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigamesregions.NodeExecutor;
import com.pauldavdesign.mineauz.minigamesregions.RegionExecutor;
import com.pauldavdesign.mineauz.minigamesregions.conditions.ConditionInterface;

public class MenuItemCondition extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;
	private ConditionInterface con;

	public MenuItemCondition(String name, Material displayItem, RegionExecutor exec, ConditionInterface con) {
		super(name, displayItem);
		this.rexec = exec;
		this.con = con;
	}

	public MenuItemCondition(String name, Material displayItem, NodeExecutor exec, ConditionInterface con) {
		super(name, displayItem);
		this.nexec = exec;
		this.con = con;
	}
	
	@Override
	public ItemStack onClick(){
		if(rexec != null){
			if(con.displayMenu(getContainer().getViewer(), getContainer(), rexec.getArguments()))
				return null;
		}
		else{
			if(con.displayMenu(getContainer().getViewer(), getContainer(), nexec.getArguments()))
				return null;
		}
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(){
		if(rexec != null)
			rexec.removeCondition(con);
		else
			nexec.removeCondition(con);
		getContainer().removeItem(getSlot());
		return null;
	}

}
