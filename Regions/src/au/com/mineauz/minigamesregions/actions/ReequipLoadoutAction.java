package au.com.mineauz.minigamesregions.actions;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ReequipLoadoutAction extends ActionInterface {

	@Override
	public String getName() {
		return "REEQUIP_LOADOUT";
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
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		if(player == null || !player.isInMinigame()) return;
		player.getLoadout().equiptLoadout(player);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return;
		player.getLoadout().equiptLoadout(player);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		// None
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		// None
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		// None
		return false;
	}

}
