package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.PlayerLoadout;

public class MenuItemSaveLoadout extends MenuItem{
	
	private PlayerLoadout loadout = null;
	private Menu altMenu = null;
	
	public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
	}
	
	public MenuItemSaveLoadout(String name, List<String> description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
	}
	
	public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
		super(name, displayItem);
		this.loadout = loadout;
		this.altMenu = altMenu;
	}
	
	public MenuItemSaveLoadout(String name, List<String> description, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
		super(name, description, displayItem);
		this.loadout = loadout;
		this.altMenu = altMenu;
	}
	
	@Override
	public ItemStack onClick(){
		ItemStack[] items = getContainer().getInventory();
		loadout.clearLoadout();
		
		for(int i = 0; i < 36; i++){
			if(items[i] != null)
				loadout.addItem(items[i], i);
		}
		for(int i = 36; i < 40; i++){
			if(items[i] != null){
				if(i == 36)
					loadout.addItem(items[i], 103);
				else if(i == 37)
					loadout.addItem(items[i], 102);
				else if(i == 38)
					loadout.addItem(items[i], 101);
				else if(i == 39)
					loadout.addItem(items[i], 100);
			}
		}
		getContainer().getViewer().sendMessage("Saved the '" + loadout.getName() + "' loadout.", null);
		if(altMenu == null)
			getContainer().getPreviousPage().displayMenu(getContainer().getViewer());
		else
			altMenu.displayMenu(getContainer().getViewer());
		return null;
	}
}
