package au.com.mineauz.minigamesregions.actions;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public abstract class ActionInterface {
	protected final ConfigPropertyContainer properties;
	
	public ActionInterface() {
		properties = new ConfigPropertyContainer();
	}
	
	public abstract String getName();
	public abstract String getCategory();
	public abstract boolean useInRegions();
	public abstract boolean useInNodes();
	public abstract void executeAction(MinigamePlayer player, TriggerArea area);
	public boolean requiresPlayer() {
		return true;
	}
	
	public abstract boolean displayMenu(MinigamePlayer player, Menu previous);
	
	public final ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public boolean canUseIn(TriggerArea area) {
		if (area instanceof Node) {
			return useInNodes();
		} else if (area instanceof Region) {
			return useInRegions();
		} else {
			return true;
		}
	}
	
	public void save(ConfigurationSection section) {
		ConfigPropertyContainer props = getProperties();
		if (props != null) {
			props.saveAll(section.createSection("arguments"));
		}
	}
	
	public void load(ConfigurationSection section) {
		ConfigPropertyContainer props = getProperties();
		if (props != null) {
			props.loadAll(section.getConfigurationSection("arguments"));
		}
	}
}
