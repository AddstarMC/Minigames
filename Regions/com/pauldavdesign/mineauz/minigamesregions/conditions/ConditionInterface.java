package com.pauldavdesign.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public interface ConditionInterface {
	
	public String getName();
	public boolean useInRegions();
	public boolean useInNodes();
	public boolean checkRegionCondition(MinigamePlayer player, Map<String, Object> args, Region region);
	public boolean checkNodeCondition(MinigamePlayer player, Map<String, Object> args, Node node);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Menu prev, Map<String, Object> args);
}
