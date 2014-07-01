package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public interface ActionInterface {
	
	public String getName();
	public String getCategory();
	public boolean useInRegions();
	public boolean useInNodes();
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event);
	public void executeNodeAction(MinigamePlayer player, Map<String, Object> args, Node node, Event event);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous);
}
