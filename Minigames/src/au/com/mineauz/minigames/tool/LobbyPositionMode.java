package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class LobbyPositionMode implements ToolMode {
	
	@Override
	public String getName() {
		return "LOBBY";
	}

	@Override
	public String getDisplayName() {
		return "Lobby Position";
	}

	@Override
	public String getDescription() {
		return "Sets the lobby;position";
	}

	@Override
	public Material getIcon() {
		return Material.TRAP_DOOR;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		minigame.setLobbyPosition(player.getLocation());
		player.sendMessage("Set lobby position.", MessageType.Normal);
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getLobbyPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getLobbyPosition(), Material.SKULL, (byte)1);
			player.sendMessage("Selected lobby position (marked with skull)", MessageType.Normal);
		}
		else{
			player.sendMessage("No lobby position set!", MessageType.Error);
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getLobbyPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getLobbyPosition(), 
					minigame.getLobbyPosition().getBlock().getType(), 
					minigame.getLobbyPosition().getBlock().getData());
			player.sendMessage("Deselected lobby position", MessageType.Normal);
		}
		else{
			player.sendMessage("No lobby position set!", MessageType.Error);
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}
}
