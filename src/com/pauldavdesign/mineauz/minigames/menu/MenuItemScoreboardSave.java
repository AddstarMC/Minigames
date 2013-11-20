package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.ScoreboardDisplay;

public class MenuItemScoreboardSave extends MenuItem{
	
	private ScoreboardDisplay disp;

	public MenuItemScoreboardSave(String name, Material displayItem, ScoreboardDisplay disp) {
		super(name, displayItem);
		this.disp = disp;
	}

	public MenuItemScoreboardSave(String name, List<String> description, Material displayItem, ScoreboardDisplay disp) {
		super(name, description, displayItem);
		this.disp = disp;
	}
	
	@Override
	public ItemStack onClick() {
		if(disp.getLocation().getBlock().getState() instanceof Sign){
			Sign sign = (Sign)disp.getLocation().getBlock().getState();
			sign.setLine(0, ChatColor.BLUE + disp.getMinigame().getName());
			sign.setLine(1, ChatColor.GREEN + MinigameUtils.capitalize(disp.getType().toString().replace("_", " ")));
			sign.setLine(2, "(" + MinigameUtils.capitalize(disp.getOrder().toString()) + ")");
			sign.setLine(3, "");
			sign.update();
			sign.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.plugin, true));
			sign.setMetadata("Minigame", new FixedMetadataValue(Minigames.plugin, disp.getMinigame().getName()));
			disp.updateStats();
			disp.getMinigame().getScoreboardData().addDisplay(disp);
		}
		getContainer().getViewer().getPlayer().closeInventory();
		return null;
	}
}
