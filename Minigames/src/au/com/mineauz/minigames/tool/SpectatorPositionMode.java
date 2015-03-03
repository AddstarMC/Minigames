package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class SpectatorPositionMode implements ToolMode{

	@Override
	public String getName() {
		return "SPECTATOR_START";
	}

	@Override
	public String getDisplayName() {
		return "Spectator Position";
	}

	@Override
	public String getDescription() {
		return "Sets the spectator;join position";
	}

	@Override
	public Material getIcon() {
		return Material.SOUL_SAND;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		minigame.setSpectatorLocation(player.getLocation());
		player.sendMessage("Set spectator start position.", MessageType.Normal);
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getSpectatorLocation() != null){
			player.getPlayer().sendBlockChange(minigame.getSpectatorLocation(), Material.SKULL, (byte)1);
			player.sendMessage("Selected spectator position (marked with skull).", MessageType.Normal);
		}
		else{
			player.sendMessage("No spectator position set!", MessageType.Error);
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getSpectatorLocation() != null){
			player.getPlayer().sendBlockChange(minigame.getSpectatorLocation(), 
					minigame.getSpectatorLocation().getBlock().getType(), 
					minigame.getSpectatorLocation().getBlock().getData());
			player.sendMessage("Spectator position deselected.", MessageType.Normal);
		}
		else{
			player.sendMessage("No spectator position set!", MessageType.Error);
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

}
