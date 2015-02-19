package au.com.mineauz.minigames.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;

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
	public MenuItemList getMenuItem(String name, Material displayItem) {
		return getMenuItem(name, displayItem, Collections.<String>emptyList());
	}

	@Override
	public MenuItemList getMenuItem(String name, Material displayItem, List<String> description) {
		List<String> values = Lists.newArrayList();
		for(T value : EnumSet.allOf(enumClass)){
			values.add(MinigameUtils.capitalize(value.name().replace('_', ' ')));
		}
		
		return new MenuItemList(name, displayItem, new Callback<String>() {
			@Override
			public void setValue(String value) {
				value = value.replace(' ', '_').toUpperCase();
				for (T v : EnumSet.allOf(enumClass)) {
					if (v.name().equalsIgnoreCase(value)) {
						setFlag(v);
						return;
					}
				}
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(getFlag().name().replace('_', ' '));
			}
		}, values);
	}

}
