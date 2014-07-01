package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class QuitAction implements ActionInterface {

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
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		if(player == null || !player.isInMinigame()) return;
		Minigames.plugin.pdata.quitMinigame(player, false);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return;
		Minigames.plugin.pdata.quitMinigame(player, false);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path) {
		
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config, String path) {
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous) {
		return false;
	}
}
