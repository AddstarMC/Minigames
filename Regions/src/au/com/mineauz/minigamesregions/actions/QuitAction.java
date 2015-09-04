package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.TriggerArea;

public class QuitAction extends ActionInterface {

	@Override
	public String getName() {
		return "QUIT";
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
		Minigames.plugin.pdata.quitMinigame(player, false);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}
}
