package au.com.mineauz.minigames.properties.types;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class LocationListProperty extends ConfigProperty<List<Location>> {
	public LocationListProperty(List<Location> value, String name) {
		super(name, value);
	}

	@Override
	public void save(ConfigurationSection root) {
		ConfigurationSection section = root.createSection(getName());
		
		int index = 0;
		for (Location location : getValue()) {
			ConfigurationSection subSection = section.createSection(String.valueOf(index));
			MinigameUtils.saveLocation(subSection, location);
			++index;
		}
	}
	
	@Override
	public void load(ConfigurationSection root) {
		ConfigurationSection section = root.getConfigurationSection(getName());
		
		List<Location> locations = Lists.newArrayList();
		for (String key : section.getKeys(false)) {
			Location location = MinigameUtils.loadLocation(section.getConfigurationSection(key));
			if (location != null) {
				locations.add(location);
			}
		}
		
		setValue(locations);
	}
}
