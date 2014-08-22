package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class RegenAreaMode implements ToolMode {

	@Override
	public String getName() {
		return "REGEN_AREA";
	}

	@Override
	public String getDisplayName() {
		return "Regeneration Area";
	}

	@Override
	public String getDescription() {
		return "Selects the regeneration;area with right click;finalise with left";
	}

	@Override
	public Material getIcon() {
		return Material.SAPLING;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(player.hasSelection()){
			minigame.setRegenArea1(player.getSelectionPoints()[0]);
			minigame.setRegenArea2(player.getSelectionPoints()[1]);
			player.sendMessage("Created a regeneration area for " + minigame, null);
		}
		else if(player.getSelectionPoints()[1] == null){
			player.sendMessage("You must make a selection with right click first!", "error");
		}
		else{
			minigame.setRegenArea1(null);
			minigame.setRegenArea2(null);
			player.sendMessage("Cleared regeneration area from " + minigame, null);
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
		if(minigame.getRegenArea1() != null && minigame.getRegenArea2() != null){
			player.setSelection(minigame.getRegenArea1(), minigame.getRegenArea2());
			player.showSelection(false);
			player.sendMessage("Selected regeneration area in " + minigame, null);
		}
		else{
			player.sendMessage("No regeneration area selected for " + minigame, "error");
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getRegenArea1() != null && minigame.getRegenArea2() != null){
			player.setSelection(minigame.getRegenArea1(), minigame.getRegenArea2());
			player.showSelection(true);
			player.sendMessage("Deselected regeneration area in " + minigame, null);
		}
		else{
			player.sendMessage("No regen area selected for " + minigame, "error");
		}
	}

}
