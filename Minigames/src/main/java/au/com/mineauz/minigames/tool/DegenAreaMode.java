package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class DegenAreaMode implements ToolMode {

	@Override
	public String getName() {
		return "DEGEN_AREA";
	}

	@Override
	public String getDisplayName() {
		return "Degeneration Area";
	}

	@Override
	public String getDescription() {
		return "Selects the degeneration;area with right click;finalise with left";
	}

	@Override
	public Material getIcon() {
		return Material.LAVA_BUCKET;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(player.hasSelection()){
			minigame.setFloorDegen1(player.getSelectionPoints()[0]);
			minigame.setFloorDegen2(player.getSelectionPoints()[1]);
			player.sendMessage("Created a degeneration area for " + minigame, null);
		}
		else if(player.getSelectionPoints()[1] == null){
			player.sendMessage("You must make a selection with right click first!", "error");
		}
		else{
			minigame.setFloorDegen1(null);
			minigame.setFloorDegen2(null);
			player.sendMessage("Cleared degeneration area from " + minigame, null);
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			player.addSelectionPoint(event.getClickedBlock().getLocation());
			if(player.getSelectionPoints()[1] != null){
				player.sendMessage("Left click to finalise selection", null);
			}
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null){
			player.setSelection(minigame.getFloorDegen1(), minigame.getFloorDegen2());
			player.showSelection(false);
			player.sendMessage("Selected degeneration area in " + minigame, null);
		}
		else{
			player.sendMessage("No degeneration area selected for " + minigame, "error");
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null){
			player.setSelection(minigame.getFloorDegen1(), minigame.getFloorDegen2());
			player.showSelection(true);
			player.sendMessage("Selected degeneration area in " + minigame, null);
		}
		else{
			player.sendMessage("No degeneration area selected for " + minigame, "error");
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

}
