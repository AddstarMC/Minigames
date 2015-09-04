package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public abstract class ConditionInterface {
	protected final ConfigPropertyContainer properties = new ConfigPropertyContainer();
	private BooleanProperty invert = new BooleanProperty(false, "invert");
	
	public ConditionInterface() {
		properties.addProperty(invert);
	}
	
	public final ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	protected void addInvertMenuItem(Menu m) {
		m.setControlItem(new MenuItemBoolean("Invert", Material.ENDER_PEARL, invert), 3);
	}
	
	public boolean isInverted() {
		return invert.getValue();
	}
	
	public abstract String getName();
	public abstract String getCategory();
	public abstract boolean useInRegions();
	public abstract boolean useInNodes();
	public abstract boolean checkCondition(MinigamePlayer player, TriggerArea area);
	public boolean requiresPlayer() {
		return true;
	}
	
	public abstract boolean displayMenu(MinigamePlayer player, Menu prev);
	
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
