package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public interface ConditionInterface {
	
	public String getName();
	public String getCategory();
	public boolean useInRegions();
	public boolean useInNodes();
	public boolean checkRegionCondition(MinigamePlayer player, Map<String, Object> args, Region region, Event event);
	public boolean checkNodeCondition(MinigamePlayer player, Map<String, Object> args, Node node, Event event);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Menu prev, Map<String, Object> args);
}
