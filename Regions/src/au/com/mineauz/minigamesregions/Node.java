package au.com.mineauz.minigamesregions;

import org.bukkit.Location;

import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

public class Node extends TriggerArea {
	private final Property<Location> location;
	
	public Node(String name, Location location) {
		super(name);
		
		this.location = Properties.create(location);
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
}
