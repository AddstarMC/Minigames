package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;

public interface RegionActionInterface {
	
	public String getName();
	public void executeAction(MinigamePlayer player, Map<String, Object> args);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous);
}
