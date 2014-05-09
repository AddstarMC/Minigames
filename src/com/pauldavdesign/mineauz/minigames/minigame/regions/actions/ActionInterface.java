package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.nodes.Node;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public interface ActionInterface {
	
	public String getName();
	public boolean useInRegions();
	public boolean useInNodes();
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region);
	public void executeNodeAction(MinigamePlayer player, Map<String, Object> args, Node node);
	public Map<String, Object> getRequiredArguments();
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path);
	public Map<String, Object> loadArguments(FileConfiguration config, String path);
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous);
}
