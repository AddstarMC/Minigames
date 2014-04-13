package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditions;

public class MenuItemConditionAdd extends MenuItem{
	
	private RegionExecutor exec;

	public MenuItemConditionAdd(String name, Material displayItem, RegionExecutor exec) {
		super(name, displayItem);
		this.exec = exec;
	}
	
	@Override
	public ItemStack onClick(){
		Menu m = new Menu(6, "Conditions", getContainer().getViewer());
		m.setPreviousPage(getContainer());
		for(String con : RegionConditions.getAllConditionNames()){
			MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(con), Material.PAPER);
			final String fcon = con;
			c.setClick(new InteractionInterface() {
				
				@Override
				public Object interact() {
					exec.addCondition(RegionConditions.getConditionByName(fcon));
					getContainer().addItem(new MenuItemCondition(fcon, Material.PAPER, exec, RegionConditions.getConditionByName(fcon)));
					getContainer().displayMenu(getContainer().getViewer());
					return null;
				}
			});
			m.addItem(c);
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, getContainer()), m.getSize() - 9);
		m.displayMenu(getContainer().getViewer());
		return null;
	}

}
