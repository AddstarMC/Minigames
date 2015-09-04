package au.com.mineauz.minigamesregions;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

public class Node extends TriggerArea {
	private final Property<Location> location;
	
	public Node(String name, Location location) {
		super(name);
		
		this.location = Properties.create(location);
	}
	
	Node(String name) {
		super(name);
		
		this.location = Properties.create();
	}
	
	public Location getLocation() {
		return location.getValue().clone();
	}
	
	public void setLocation(Location location) {
		this.location.setValue(location);
	}
	
	public Property<Location> location() {
		return location;
	}
	
	@Override
	public void save(ConfigurationSection section) {
		MinigameUtils.saveLocation(section.createSection("point"), location.getValue());
		super.save(section);
	}
	
	@Override
	public void load(ConfigurationSection section) {
		location.setValue(MinigameUtils.loadLocation(section.getConfigurationSection("point")));
		super.load(section);
	}
}
