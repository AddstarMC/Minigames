package au.com.mineauz.minigames.menu;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;

public class MenuItemScoreboardSave extends MenuItem{
	
	private ScoreboardDisplay disp;

	public MenuItemScoreboardSave(String name, Material displayItem, ScoreboardDisplay disp) {
		super(name, displayItem);
		this.disp = disp;
	}

	public MenuItemScoreboardSave(String name, String description, Material displayItem, ScoreboardDisplay disp) {
		super(name, description, displayItem);
		this.disp = disp;
	}
	
	@Override
	public void onClick(MinigamePlayer player) {
		disp.placeRootSign();
		disp.getMinigame().getScoreboardData().reload(disp.getRoot().getBlock());
		
		player.getPlayer().closeInventory();
	}
}
