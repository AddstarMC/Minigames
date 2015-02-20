package au.com.mineauz.minigames.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class SimpleLocationFlag extends Flag<Location>{

	public SimpleLocationFlag(Location value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}
	
	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName() + ".x", getFlag().getX());
		config.set(path + "." + getName() + ".y", getFlag().getY());
		config.set(path + "." + getName() + ".z", getFlag().getZ());
		config.set(path + "." + getName() + ".world", getFlag().getWorld().getName());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		double x = config.getDouble(path + "." + getName() + ".x");
		double y = config.getDouble(path + "." + getName() + ".y");
		double z = config.getDouble(path + "." + getName() + ".z");
		String world = config.getString(path + "." + getName() + ".world");
		
		setFlag(new Location(Bukkit.getWorld(world), x, y, z));
	}
}
