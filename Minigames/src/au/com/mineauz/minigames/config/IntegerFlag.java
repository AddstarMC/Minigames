package au.com.mineauz.minigames.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemInteger;

public class IntegerFlag extends Flag<Integer>{
	
	public IntegerFlag(Integer value, String name){
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
		setFlag(config.getInt(path + "." + getName()));
	}

	@Override
	public MenuItem getMenuItem(String name, String description, Material displayItem) {
		return new MenuItemInteger(name, description, displayItem, getCallback(), 0, Integer.MAX_VALUE);
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem) {
		return new MenuItemInteger(name, description, displayItem, getCallback(), 0, Integer.MAX_VALUE);
	}
	
	public MenuItem getMenuItem(String name, Material displayItem, int min, int max) {
		return getMenuItem(name, null, displayItem, min, max);
	}
	
	public MenuItem getMenuItem(String name, String description, Material displayItem, int min, int max) {
		return new MenuItemInteger(name, description, displayItem, getCallback(), min, max);
	}

	public MenuItem getMenuItem(String name, MaterialData displayItem, int min, int max) {
		return getMenuItem(name, null, displayItem, min, max);
	}
	
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem, int min, int max) {
		return new MenuItemInteger(name, description, displayItem, getCallback(), min, max);
	}
}
