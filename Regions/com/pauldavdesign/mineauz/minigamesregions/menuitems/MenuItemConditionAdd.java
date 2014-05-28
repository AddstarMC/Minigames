package com.pauldavdesign.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.NodeExecutor;
import com.pauldavdesign.mineauz.minigamesregions.RegionExecutor;
import com.pauldavdesign.mineauz.minigamesregions.conditions.Conditions;

public class MenuItemConditionAdd extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;

	public MenuItemConditionAdd(String name, Material displayItem, RegionExecutor exec) {
		super(name, displayItem);
		this.rexec = exec;
	}

	public MenuItemConditionAdd(String name, Material displayItem, NodeExecutor exec) {
		super(name, displayItem);
		this.nexec = exec;
	}
	
	@Override
	public ItemStack onClick(){
		Menu m = new Menu(6, "Conditions", getContainer().getViewer());
		m.setPreviousPage(getContainer());
		Map<String, Menu> cats = new HashMap<String, Menu>();
		List<String> cons = new ArrayList<String>(Conditions.getAllConditionNames());
		Collections.sort(cons);
		for(String con : cons){
			if((Conditions.getConditionByName(con).useInNodes() && nexec != null) || 
					(Conditions.getConditionByName(con).useInRegions() && rexec != null)){
				String catname = Conditions.getConditionByName(con).getCategory();
				if(catname == null)
					catname = "misc conditions";
				catname.toLowerCase();
				Menu cat = null;
				if(!cats.containsKey(catname)){
					cat = new Menu(6, MinigameUtils.capitalize(catname), getContainer().getViewer());
					cats.put(catname, cat);
					m.addItem(new MenuItemPage(MinigameUtils.capitalize(catname), Material.CHEST, cat));
					cat.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, m), cat.getSize() - 9);
				}
				else
					cat = cats.get(catname);
				MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(con), Material.PAPER);
				final String fcon = con;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact(Object object) {
						if(rexec != null){
							rexec.addCondition(Conditions.getConditionByName(fcon));
							getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, rexec, Conditions.getConditionByName(fcon)));
						}
						else{
							nexec.addCondition(Conditions.getConditionByName(fcon));
							getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, nexec, Conditions.getConditionByName(fcon)));
						}
						getContainer().displayMenu(getContainer().getViewer());
						return null;
					}
				});
				cat.addItem(c);
			}
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, getContainer()), m.getSize() - 9);
		m.displayMenu(getContainer().getViewer());
		return null;
	}

}
