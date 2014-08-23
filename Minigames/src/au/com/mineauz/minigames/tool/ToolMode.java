package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public interface ToolMode {
	
	public String getName();
	public String getDisplayName();
	public String getDescription();
	public Material getIcon();
	public void onSetMode(MinigamePlayer player, MinigameTool tool);
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool);
	public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);
	public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);
	public void select(MinigamePlayer player, Minigame minigame, Team team);
	public void deselect(MinigamePlayer player, Minigame minigame, Team team);
}
