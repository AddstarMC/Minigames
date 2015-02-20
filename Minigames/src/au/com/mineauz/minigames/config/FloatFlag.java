package au.com.mineauz.minigames.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDecimal;

public class FloatFlag extends Flag<Float>{
	
	public FloatFlag(Float value, String name){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		config.set(path + "." + getName(), getFlag().doubleValue());
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		setFlag(((Double)config.getDouble(path + "." + getName())).floatValue());
	}

	public Callback<Double> getWrappedCallback() {
		return new Callback<Double>() {
			
			@Override
			public void setValue(Double value) {
				setFlag(value.floatValue());
			}
			
			@Override
			public Double getValue() {
				return getFlag().doubleValue();
			}
		};
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, Material displayItem) {
		return new MenuItemDecimal(name, description, displayItem, getWrappedCallback(), 1d, 1d, 0d, Double.MAX_VALUE);
	}
	
	@Override
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem) {
		return new MenuItemDecimal(name, description, displayItem, getWrappedCallback(), 1d, 1d, 0d, Double.MAX_VALUE);
	}
	
	public MenuItem getMenuItem(String name, Material displayItem, double lowerinc, double upperinc, double min, double max) {
		return getMenuItem(name, null, displayItem, lowerinc, upperinc, min, max);
	}

	public MenuItem getMenuItem(String name, String description, Material displayItem, double lowerinc, double upperinc, double min, double max) {
		return new MenuItemDecimal(name, description, displayItem, getWrappedCallback(), lowerinc, upperinc, min, max);
	}
	
	public MenuItem getMenuItem(String name, MaterialData displayItem, double lowerinc, double upperinc, double min, double max) {
		return getMenuItem(name, null, displayItem, lowerinc, upperinc, min, max);
	}
	
	public MenuItem getMenuItem(String name, String description, MaterialData displayItem, double lowerinc, double upperinc, double min, double max) {
		return new MenuItemDecimal(name, description, displayItem, getWrappedCallback(), lowerinc, upperinc, min, max);
	}

}
