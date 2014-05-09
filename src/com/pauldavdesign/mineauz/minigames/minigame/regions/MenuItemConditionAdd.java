package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.nodes.NodeExecutor;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.Conditions;

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
		for(String con : Conditions.getAllConditionNames()){
			if((Conditions.getConditionByName(con).useInNodes() && nexec != null) || 
					(Conditions.getConditionByName(con).useInRegions() && rexec != null)){
				MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(con), Material.PAPER);
				final String fcon = con;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact() {
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
				m.addItem(c);
			}
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, getContainer()), m.getSize() - 9);
		m.displayMenu(getContainer().getViewer());
		return null;
	}

}
