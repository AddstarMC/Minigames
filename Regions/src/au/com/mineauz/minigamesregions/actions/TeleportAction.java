package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.TriggerArea;

public class TeleportAction extends ActionInterface{

	@Override
	public String getName() {
		return "TELEPORT";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}
	
	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Node) {
			player.teleport(((Node)area).getLocation());
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
