package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemString extends MenuItemValue<String> {
	private boolean allowNull = false;

	public MenuItemString(String name, MaterialData displayItem, Callback<String> callback) {
		super(name, displayItem, callback);
	}
	public MenuItemString(String name, String description, Material displayItem, Callback<String> callback) {
		super(name, description, displayItem, callback);
	}
	public MenuItemString(String name, Material displayItem, Callback<String> callback) {
		super(name, null, displayItem, callback);
	}
	public MenuItemString(String name, String description, MaterialData displayItem, Callback<String> callback) {
		super(name, description, displayItem, callback);
	}
	
	public void setAllowNull(boolean allow){
		allowNull = allow;
	}
	
	@Override
	protected List<String> getValueDescription(String value) {
		if (value == null) {
			return Arrays.asList(ChatColor.RED + "Not Set");
		} else {
			return Arrays.asList(ChatColor.GREEN + StringUtils.abbreviate(value, 20));
		}
	}
	
	@Override
	protected String increaseValue(String current, boolean shift) {
		return current;
	}
	
	@Override
	protected String decreaseValue(String current, boolean shift) {
		return current;
	}
	
	@Override
	protected boolean isManualEntryAllowed() {
		return true;
	}
	
	@Override
	protected String getManualEntryText() {
		return "Enter string value into chat for " + getName();
	}
	
	@Override
	protected int getManualEntryTime() {
		return 40;
	}
	
	@Override
	protected void onManualEntryStart(MinigamePlayer player) {
		if(allowNull){
			player.sendMessage("Enter \"null\" to remove the string value");
		}
	}
	
	@Override
	protected String onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {
		if (allowNull && raw.equalsIgnoreCase("null")) {
			return null;
		} else {
			return raw;
		}
		
	}
}
