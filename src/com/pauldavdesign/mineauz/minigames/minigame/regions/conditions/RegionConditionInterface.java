package com.pauldavdesign.mineauz.minigames.minigame.regions.conditions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public interface RegionConditionInterface {
	
	public String getName();
	public boolean checkCondition(MinigamePlayer player, Map<String, Object> args);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Menu prev, RegionExecutor exec);
}
