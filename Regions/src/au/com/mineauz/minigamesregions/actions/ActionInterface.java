package au.com.mineauz.minigamesregions.actions;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public abstract class ActionInterface {
	protected final ConfigPropertyContainer properties;
	
	public ActionInterface() {
		properties = new ConfigPropertyContainer();
	}
	
	public abstract String getName();
	public abstract String getCategory();
	public abstract boolean useInRegions();
	public abstract boolean useInNodes();
	public abstract void executeRegionAction(MinigamePlayer player, Region region);
	public abstract void executeNodeAction(MinigamePlayer player, Node node);
	public abstract void saveArguments(FileConfiguration config, String path);
	public abstract void loadArguments(FileConfiguration config, String path);
	public abstract boolean displayMenu(MinigamePlayer player, Menu previous);
	
	public final ConfigPropertyContainer getProperties() {
		return properties;
	}
}
