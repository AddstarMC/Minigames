package au.com.mineauz.minigames.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

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

	@Override
	public MenuItem getMenuItem(String name, Material displayItem) {
		MenuItemDecimal dec = new MenuItemDecimal(name, displayItem, new Callback<Double>() {
			
			@Override
			public void setValue(Double value) {
				setFlag(value.floatValue());
			}
			
			@Override
			public Double getValue() {
				return getFlag().doubleValue();
			}
		}, 1d, 1d, 0d, null);
		return dec;
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem,
			List<String> description) {
		MenuItemDecimal dec = new MenuItemDecimal(name, description, displayItem, new Callback<Double>() {
			
			@Override
			public void setValue(Double value) {
				setFlag(value.floatValue());
			}
			
			@Override
			public Double getValue() {
				return getFlag().doubleValue();
			}
		}, 1d, 1d, 0d, null);
		return dec;
	}

}
