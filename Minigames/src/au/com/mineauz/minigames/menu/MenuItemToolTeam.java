package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.tool.MinigameTool;

public class MenuItemToolTeam extends MenuItemList{
	
	private Callback<String> value;

	public MenuItemToolTeam(String name, Material displayItem, Callback<String> value, List<String> options) {
		super(name, displayItem, value, options);
		this.value = value;
	}
	
	@Override
	protected void onChange(MinigamePlayer player, String previous, String current) {
		if(MinigameUtils.hasMinigameTool(player)) {
			MinigameTool tool = MinigameUtils.getMinigameTool(player);
			tool.setTeam(TeamColor.matchColor(value.getValue().replace(' ', '_')));
		}
	}
}
