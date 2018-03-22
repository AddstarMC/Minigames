package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ToolMode {
	
	String getName();
	String getDisplayName();
	String getDescription();
	Material getIcon();
	void onSetMode(MinigamePlayer player, MinigameTool tool);
	void onUnsetMode(MinigamePlayer player, MinigameTool tool);
	void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);
	void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event);
	
	void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event);
	
	void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event);
	void select(MinigamePlayer player, Minigame minigame, Team team);
	void deselect(MinigamePlayer player, Minigame minigame, Team team);
}
