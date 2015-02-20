package au.com.mineauz.minigames.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemEnum;

public class EnumFlag<T extends Enum<T>> extends Flag<T> {
	
	private Class<T> enumClass;
	
	@SuppressWarnings("unchecked")
	public EnumFlag(T value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
		enumClass = (Class<T>) value.getClass();
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName(), getFlag().name());
	}
	
	@Override
	public void loadValue(String path, FileConfiguration config) {
		setFlag(T.valueOf(enumClass, config.getString(path + "." + getName())));
	}

	@Override
	public MenuItemEnum<T> getMenuItem(String name, String description, Material displayItem) {
		return new MenuItemEnum<T>(name, description, displayItem, getCallback(), enumClass);
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem) {
		return new MenuItemEnum<T>(name, description, displayItem, getCallback(), enumClass);
	}

}
