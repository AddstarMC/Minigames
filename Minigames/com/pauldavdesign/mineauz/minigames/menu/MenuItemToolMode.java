package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameTool;
import com.pauldavdesign.mineauz.minigames.MinigameToolMode;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;

public class MenuItemToolMode extends MenuItem{
	
	private MinigameToolMode mode;

	public MenuItemToolMode(String name, Material displayItem, MinigameToolMode mode) {
		super(name, displayItem);
		this.mode = mode;
	}

	public MenuItemToolMode(String name, List<String> description, Material displayItem, MinigameToolMode mode) {
		super(name, description, displayItem);
		this.mode = mode;
	}
	
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		if(MinigameUtils.hasMinigameTool(ply)){
			MinigameTool tool = MinigameUtils.getMinigameTool(ply);
			tool.setMode(mode);
		}
		return getItem();
	}
}
