package au.com.mineauz.minigames.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
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
		if(disp.getLocation().getBlock().getState() instanceof Sign){
			Sign sign = (Sign)disp.getLocation().getBlock().getState();
			sign.setLine(0, ChatColor.BLUE + disp.getMinigame().getName(false));
			sign.setLine(1, ChatColor.GREEN + MinigameUtils.capitalize(disp.getType().toString().replace("_", " ")));
			sign.setLine(2, "(" + MinigameUtils.capitalize(disp.getOrder().toString()) + ")");
			sign.setLine(3, "");
			sign.update();
			sign.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.plugin, true));
			sign.setMetadata("Minigame", new FixedMetadataValue(Minigames.plugin, disp.getMinigame().getName(false)));
			disp.updateStats();
			disp.getMinigame().getScoreboardData().addDisplay(disp);
		}
		player.getPlayer().closeInventory();
	}
}
