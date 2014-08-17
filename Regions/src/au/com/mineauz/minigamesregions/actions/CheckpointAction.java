package au.com.mineauz.minigamesregions.actions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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
	public void executeRegionAction(MinigamePlayer player,
			Region region, Event event) {
		execute(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node, Event event) {
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		if(player == null || !player.isInMinigame()) return;
		player.setCheckpoint(player.getLocation());
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
