package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameTool;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;

public class MenuItemToolTeam extends MenuItemList{
	
	private Callback<String> value;

	public MenuItemToolTeam(String name, Material displayItem, Callback<String> value, List<String> options) {
		super(name, displayItem, value, options);
		this.value = value;
	}
	
	@Override
	public ItemStack onClick() {
		super.onClick();
		MinigamePlayer ply = getContainer().getViewer();
		if(MinigameUtils.hasMinigameTool(ply)){
			MinigameTool tool = MinigameUtils.getMinigameTool(ply);
			tool.setTeam(value.getValue());
		}
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick() {
		super.onRightClick();
		MinigamePlayer ply = getContainer().getViewer();
		if(MinigameUtils.hasMinigameTool(ply)){
			MinigameTool tool = MinigameUtils.getMinigameTool(ply);
			tool.setTeam(value.getValue());
		}
		return getItem();
	}
}
