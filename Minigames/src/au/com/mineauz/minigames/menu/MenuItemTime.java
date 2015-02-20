package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemTime extends MenuItemInteger{

	public MenuItemTime(String name, Material displayItem, Callback<Integer> value, int min, int max) {
		super(name, displayItem, value, min, max);
	}

	public MenuItemTime(String name, String description, Material displayItem, Callback<Integer> value, int min, int max) {
		super(name, description, displayItem, value, min, max);
	}
	
	@Override
	protected List<String> getValueDescription(Integer value) {
		return Arrays.asList(ChatColor.GREEN + MinigameUtils.convertTime(value, true));
	}
}
