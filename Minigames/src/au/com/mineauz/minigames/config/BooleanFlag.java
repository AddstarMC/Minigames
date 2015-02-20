package au.com.mineauz.minigames.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;

public class BooleanFlag extends Flag<Boolean>{
	
	public BooleanFlag(boolean value, String name){
		setFlag(value);
		setName(name);
		setDefaultFlag(value);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName(), getFlag());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		setFlag(config.getBoolean(path + "." + getName()));
	}
	
	@Override
	public MenuItemBoolean getMenuItem(String name, String description, Material displayItem){
		return new MenuItemBoolean(name, description, displayItem, getCallback());
	}

	@Override
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem) {
		return new MenuItemBoolean(name, description, displayItem, getCallback());
	}
}
