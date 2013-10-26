package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MenuItemSaveMinigame extends MenuItem{
	private Minigame mgm = null;
	
	public MenuItemSaveMinigame(String name, Material displayItem, Minigame minigame) {
		super(name, displayItem);
		mgm = minigame;
	}
	
	public MenuItemSaveMinigame(String name, List<String> description, Material displayItem, Minigame minigame) {
		super(name, description, displayItem);
		mgm = minigame;
	}
	
	@Override
	public ItemStack onClick(){
		mgm.saveMinigame();
		getContainer().getViewer().sendMessage("Saved the '" + mgm.getName() + "' Minigame.", null);
		return null;
	}

}
