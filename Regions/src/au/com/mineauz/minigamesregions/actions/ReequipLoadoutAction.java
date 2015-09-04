package au.com.mineauz.minigamesregions.actions;

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
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		// None
		return false;
	}

}
