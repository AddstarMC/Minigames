package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemList extends MenuItemValue<String> {
	
	private List<String> options = null;
	
	public MenuItemList(String name, MaterialData displayItem, Callback<String> callback, List<String> options) {
		super(name, displayItem, callback);
		this.options = options;
		updateDescription();
	}
	public MenuItemList(String name, String description, Material displayItem, Callback<String> callback, List<String> options) {
		super(name, description, displayItem, callback);
		this.options = options;
		updateDescription();
	}
	public MenuItemList(String name, Material displayItem, Callback<String> callback, List<String> options) {
		super(name, null, displayItem, callback);
		this.options = options;
		updateDescription();
	}
	public MenuItemList(String name, String description, MaterialData displayItem, Callback<String> callback, List<String> options) {
		super(name, description, displayItem, callback);
		this.options = options;
		updateDescription();
	}
	
	public MenuItemList setOptions(List<String> options) {
		this.options = options;
		updateDescription();
		return this;
	}
	
	@Override
	protected List<String> getValueDescription(String value) {
		// For the initial update
		if (options == null) {
			return Collections.emptyList();
		}
		
		if (options.isEmpty()) {
			return Collections.emptyList();
		}
		
		int position = options.indexOf(value);
		if (position == -1) {
			return Arrays.asList(ChatColor.RED + "*ERROR*");
		}
		
		int last = position - 1;
		int next = position + 1;
		if (last < 0) {
			last = options.size() - 1;
		}
		if (next >= options.size()) {
			next = 0;
		}
		
		List<String> description = Lists.newArrayListWithCapacity(3);
		description.add(ChatColor.GRAY + options.get(last));
		description.add(ChatColor.GREEN + options.get(position));
		description.add(ChatColor.GRAY + options.get(next));
		
		return description;
	}
	
	@Override
	protected String increaseValue(String current, boolean shift) {
		if (options.isEmpty()) {
			return null;
		}
		
		int index = options.indexOf(current);
		if (index == -1) {
			return options.get(0);
		}
		
		++index;
		if (index >= options.size()) {
			index = 0;
		}
		
		return options.get(index);
	}
	
	@Override
	protected String decreaseValue(String current, boolean shift) {
		if (options.isEmpty()) {
			return null;
		}
		
		int index = options.indexOf(current);
		if (index == -1) {
			return options.get(0);
		}
		
		--index;
		if (index < 0) {
			index = options.size() - 1;
		}
		
		return options.get(index);
	}
	
	@Override
	protected boolean isManualEntryAllowed() {
		return true;
	}
	
	@Override
	protected String getManualEntryText() {
		return "Enter the name of the option into chat for " + getName();
	}
	
	@Override
	protected int getManualEntryTime() {
		return 20;
	}
	
	@Override
	protected void onManualEntryStart(MinigamePlayer player) {
		player.sendMessage("Possible Options: " + MinigameUtils.listToString(options));
	}
	
	@Override
	protected String onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {
		for(String opt : options) {
			if(opt.equalsIgnoreCase(raw)) {
				return opt;
			}
		}
		throw new IllegalArgumentException("Could not find matching option!");
	}
}
