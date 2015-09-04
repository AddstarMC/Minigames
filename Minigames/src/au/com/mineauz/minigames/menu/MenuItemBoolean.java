package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.properties.ObservableValue;

public class MenuItemBoolean extends MenuItemValue<Boolean> {
	public MenuItemBoolean(String name, MaterialData displayItem, ObservableValue<Boolean> observable) {
		super(name, displayItem, observable);
	}
	public MenuItemBoolean(String name, String description, Material displayItem, ObservableValue<Boolean> observable) {
		super(name, description, displayItem, observable);
	}
	public MenuItemBoolean(String name, Material displayItem, ObservableValue<Boolean> observable) {
		super(name, null, displayItem, observable);
	}
	public MenuItemBoolean(String name, String description, MaterialData displayItem, ObservableValue<Boolean> observable) {
		super(name, description, displayItem, observable);
	}
	
	@Override
	protected List<String> getValueDescription(Boolean value) {
		if (value) {
			return Arrays.asList(ChatColor.GREEN + "True");
		} else {
			return Arrays.asList(ChatColor.GREEN + "False");
		}
	}
	
	@Override
	protected Boolean increaseValue(Boolean current, boolean shift) {
		return !current;
	}
	
	@Override
	protected Boolean decreaseValue(Boolean current, boolean shift) {
		return !current;
	}
}
