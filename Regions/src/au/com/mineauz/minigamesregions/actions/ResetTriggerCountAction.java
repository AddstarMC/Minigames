package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.TriggerArea;
import au.com.mineauz.minigamesregions.TriggerExecutor;

public class ResetTriggerCountAction extends ActionInterface{

	@Override
	public String getName() {
		return "RESET_TRIGGER_COUNT";
	}

	@Override
	public String getCategory() {
		return "Region/Node Actions";
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
	public boolean requiresPlayer() {
		return false;
	}
	
	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		for (TriggerExecutor executor : area.getExecutors()) {
			executor.setTriggerCount(0);
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
