package com.pauldavdesign.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;

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
	public MenuItem getMenuItem(String name, Material displayItem){
		return new MenuItemBoolean(name, displayItem, new Callback<Boolean>() {
			
			@Override
			public void setValue(Boolean value) {
				setFlag(value);
			}
			
			@Override
			public Boolean getValue() {
				return getFlag();
			}
		});
	}
	
	@Override
	public MenuItem getMenuItem(String name, Material displayItem, List<String> description){
		return new MenuItemBoolean(name, description, displayItem, new Callback<Boolean>() {
			
			@Override
			public void setValue(Boolean value) {
				setFlag(value);
			}
			
			@Override
			public Boolean getValue() {
				return getFlag();
			}
		});
	}

}
