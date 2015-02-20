package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;

public class MenuItemToolMode extends MenuItem{
	
	private ToolMode mode;

	public MenuItemToolMode(String name, Material displayItem, ToolMode mode) {
		super(name, displayItem);
		this.mode = mode;
	}

	public MenuItemToolMode(String name, String description, Material displayItem, ToolMode mode) {
		super(name, description, displayItem);
		this.mode = mode;
	}
	
	public void onClick(MinigamePlayer ply){
		if(MinigameUtils.hasMinigameTool(ply)){
			MinigameTool tool = MinigameUtils.getMinigameTool(ply);
			if(tool.getMode() != null)
				tool.getMode().onUnsetMode(ply, tool);
			tool.setMode(mode);
			tool.getMode().onSetMode(ply, tool);
		}
	}
}
