package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class QuitPositionMode implements ToolMode{

	@Override
	public String getName() {
		return "QUIT";
	}

	@Override
	public String getDisplayName() {
		return "Quit Position";
	}

	@Override
	public String getDescription() {
		return "Sets the quit;position";
	}

	@Override
	public Material getIcon() {
		return Material.WOOD_DOOR;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		minigame.setQuitPosition(player.getLocation());
		player.sendMessage("Set quit position.", null);
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getQuitPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getQuitPosition(), Material.SKULL, (byte)1);
			player.sendMessage("Selected quit position (marked with skull)", null);
		}
		else{
			player.sendMessage("No quit position set!", "error");
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getQuitPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getQuitPosition(), 
					minigame.getQuitPosition().getBlock().getType(), 
					minigame.getQuitPosition().getBlock().getData());
			player.sendMessage("Deselected quit position", null);
		}
		else{
			player.sendMessage("No quit position set!", "error");
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

}
