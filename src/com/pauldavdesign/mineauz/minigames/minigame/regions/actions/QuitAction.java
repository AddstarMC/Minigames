package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.nodes.Node;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class QuitAction implements ActionInterface {

	@Override
	public String getName() {
		return "QUIT";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region) {
		if(player.isInMinigame())
			Minigames.plugin.pdata.quitMinigame(player, false);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path) {
		
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config, String path) {
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous) {
		return false;
	}

}
