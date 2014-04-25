package com.pauldavdesign.mineauz.minigames.minigame.regions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.regions.actions.RegionActions;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditions;

public class MenuItemActionAdd extends MenuItem{
	
	private RegionExecutor exec;

	public MenuItemActionAdd(String name, Material displayItem, RegionExecutor exec) {
		super(name, displayItem);
		this.exec = exec;
	}
	
	@Override
	public ItemStack onClick(){
		Menu m = new Menu(6, "Actions", getContainer().getViewer());
		m.setPreviousPage(getContainer());
		for(String act : RegionActions.getAllActionNames()){
			MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(act), Material.PAPER);
			final String fact = act;
			c.setClick(new InteractionInterface() {
				
				@Override
				public Object interact() {
					exec.addCondition(RegionConditions.getConditionByName(fact));
					getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, exec, RegionActions.getActionByName(fact)));
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
