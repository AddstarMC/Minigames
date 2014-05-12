package com.pauldavdesign.mineauz.minigamesregions.menuitems;

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
import com.pauldavdesign.mineauz.minigamesregions.actions.Actions;

public class MenuItemActionAdd extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;

	public MenuItemActionAdd(String name, Material displayItem, RegionExecutor exec) {
		super(name, displayItem);
		this.rexec = exec;
	}
	
	public MenuItemActionAdd(String name, Material displayItem, NodeExecutor exec) {
		super(name, displayItem);
		this.nexec = exec;
	}
	
	@Override
	public ItemStack onClick(){
		Menu m = new Menu(6, "Actions", getContainer().getViewer());
		m.setPreviousPage(getContainer());
		for(String act : Actions.getAllActionNames()){
			if((Actions.getActionByName(act).useInNodes() && nexec != null) || (Actions.getActionByName(act).useInRegions() && rexec != null)){
				MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(act), Material.PAPER);
				final String fact = act;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact() {
						if(nexec == null){
							rexec.addAction(Actions.getActionByName(fact));
							getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, rexec, Actions.getActionByName(fact)));
							getContainer().displayMenu(getContainer().getViewer());
						}
						else{
							nexec.addAction(Actions.getActionByName(fact));
							getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, nexec, Actions.getActionByName(fact)));
							getContainer().displayMenu(getContainer().getViewer());
						}
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
