package au.com.mineauz.minigames.properties.types;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class LocationProperty extends ConfigProperty<Location> {
	public LocationProperty(Location value, String name) {
		super(name, value);
	}
	
	

	@Override
	public void save(ConfigurationSection section) {
		if (getValue() != null) {
			MinigameUtils.saveLocation(section.createSection(getName()), getValue());
		}
	}
	
	@Override
	public void load(ConfigurationSection section) {
		Location location = MinigameUtils.loadLocation(section.getConfigurationSection(getName()));
		
		if (location != null) {
			setValue(location);
		}
	}
}
