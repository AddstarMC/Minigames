package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemEnum<T extends Enum<T>> extends MenuItemValue<T> {
	
	private List<T> enumList;
	
	public MenuItemEnum(String name, MaterialData displayItem, Callback<T> callback, Class<T> enumClass) {
		super(name, displayItem, callback);
		enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
		updateDescription();
	}
	public MenuItemEnum(String name, String description, Material displayItem, Callback<T> callback, Class<T> enumClass) {
		super(name, description, displayItem, callback);
		enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
		updateDescription();
	}
	public MenuItemEnum(String name, Material displayItem, Callback<T> callback, Class<T> enumClass) {
		super(name, null, displayItem, callback);
		enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
		updateDescription();
	}
	public MenuItemEnum(String name, String description, MaterialData displayItem, Callback<T> callback, Class<T> enumClass) {
		super(name, description, displayItem, callback);
		enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
		updateDescription();
	}
	
	@Override
	protected List<String> getValueDescription(T value) {
		// For the initial update
		if (enumList == null) {
			return Collections.emptyList();
		}
		
		if (enumList.isEmpty()) {
			return Collections.emptyList();
		}
		
		int position = enumList.indexOf(value);
		if (position == -1) {
			return Arrays.asList(ChatColor.RED + "*ERROR*");
		}
		
		int last = position - 1;
		int next = position + 1;
		if (last < 0) {
			last = enumList.size() - 1;
		}
		if (next >= enumList.size()) {
			next = 0;
		}
		
		List<String> options = Lists.newArrayListWithCapacity(3);
		options.add(ChatColor.GRAY + getEnumName(enumList.get(last)));
		options.add(ChatColor.GREEN + getEnumName(enumList.get(position)));
		options.add(ChatColor.GRAY + getEnumName(enumList.get(next)));
		
		return options;
	}
	
	private String getEnumName(T val) {
		return MinigameUtils.capitalize(val.name().replace('_', ' '));
	}
	
	@Override
	protected T increaseValue(T current, boolean shift) {
		if (enumList.isEmpty()) {
			return null;
		}
		
		int index = enumList.indexOf(current);
		if (index == -1) {
			return enumList.get(0);
		}
		
		++index;
		if (index >= enumList.size()) {
			index = 0;
		}
		
		return enumList.get(index);
	}
	
	@Override
	protected T decreaseValue(T current, boolean shift) {
		if (enumList.isEmpty()) {
			return null;
		}
		
		int index = enumList.indexOf(current);
		if (index == -1) {
			return enumList.get(0);
		}
		
		--index;
		if (index < 0) {
			index = enumList.size() - 1;
		}
		
		return enumList.get(index);
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
		StringBuilder builder = new StringBuilder();
		builder.append("Possible Options: ");
		boolean first = true;
		boolean odd = true;
		for (T v : enumList) {
			if (!first) {
				builder.append(ChatColor.WHITE);
				builder.append(", ");
			}
			
			if (odd) {
				builder.append(ChatColor.WHITE);
			} else {
				builder.append(ChatColor.GRAY);
			}
			
			odd = !odd;
			first = false;
			
			builder.append(v.name().toLowerCase());
		}
		
		player.sendMessage(builder.toString());
	}
	
	@Override
	protected T onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {
		for(T v : enumList) {
			if (v.name().equalsIgnoreCase(raw)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Could not find matching option!");
	}
}
