package au.com.mineauz.minigamesregions.actions;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.TriggerArea;

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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
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
