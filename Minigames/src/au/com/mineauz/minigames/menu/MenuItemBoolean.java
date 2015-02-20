package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class MenuItemBoolean extends MenuItemValue<Boolean> {
	
	public MenuItemBoolean(String name, MaterialData displayItem, Callback<Boolean> callback) {
		super(name, displayItem, callback);
	}
	public MenuItemBoolean(String name, String description, Material displayItem, Callback<Boolean> callback) {
		super(name, description, displayItem, callback);
	}
	public MenuItemBoolean(String name, Material displayItem, Callback<Boolean> callback) {
		super(name, null, displayItem, callback);
	}
	public MenuItemBoolean(String name, String description, MaterialData displayItem, Callback<Boolean> callback) {
		super(name, description, displayItem, callback);
	}
	
	@Override
	protected List<String> getValueDescription(Boolean value) {
		if(getValue()) {
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
