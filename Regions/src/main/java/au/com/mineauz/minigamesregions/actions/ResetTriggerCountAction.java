package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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
	public void describe(Map<String, Object> out) {
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		debug(player,region);
		for(BaseExecutor ex : region.getExecutors())
			ex.setTriggerCount(0);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		debug(player,node);
		for(BaseExecutor ex : node.getExecutors())
			ex.setTriggerCount(0);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
