package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.TriggerArea;

public class CheckpointAction extends ActionInterface {

	@Override
	public String getName() {
		return "CHECKPOINT";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		player.setCheckpoint(player.getLocation());
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
