package au.com.mineauz.minigames.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;

public class StringFlag extends Flag<String>{
	
	public StringFlag(String value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName(), getFlag());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		setFlag(config.getString(path + "." + getName()));
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, Material displayItem) {
		return new MenuItemString(name, description, displayItem, getCallback());
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem) {
		return new MenuItemString(name, description, displayItem, getCallback());
	}
}
