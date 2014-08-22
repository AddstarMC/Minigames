package au.com.mineauz.minigamesregions;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.ToolMode;

public class RegionToolMode implements ToolMode {

	@Override
	public String getName() {
		return "REGION";
	}

	@Override
	public String getDisplayName() {
		return "Region Selection";
	}

	@Override
	public String getDescription() {
		return "Selects an area;for a region.;Create via command";
	}

	@Override
	public Material getIcon() {
		return Material.DIAMOND_BLOCK;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			player.addSelectionPoint(event.getClickedBlock().getLocation());
			if(player.hasSelection()){
				player.sendMessage("Selection complete, finalise with:\n'/minigame set " + minigame + " region create <Name>'", null);
			}
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		player.sendMessage("Nothing to select (Regions are not a single selection like other things)", "error");
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		player.clearSelection();
		player.sendMessage("Cleared selection.", null);
	}

}
