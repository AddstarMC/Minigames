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
		Map<String, Menu> cats = new HashMap<String, Menu>();
		List<String> acts = new ArrayList<String>(Actions.getAllActionNames());
		Collections.sort(acts);
		for(String act : acts){
			if((Actions.getActionByName(act).useInNodes() && nexec != null) || (Actions.getActionByName(act).useInRegions() && rexec != null)){
				String catname = Actions.getActionByName(act).getCategory();
				if(catname == null)
					catname = "misc";
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
				MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(act), Material.PAPER);
				final String fact = act;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact(Object object) {
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
				cat.addItem(c);
			}
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, getContainer()), m.getSize() - 9);
		m.displayMenu(getContainer().getViewer());
		return null;
	}
}
