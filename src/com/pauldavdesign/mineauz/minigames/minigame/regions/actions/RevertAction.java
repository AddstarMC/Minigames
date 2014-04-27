package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class RevertAction implements RegionActionInterface {

	@Override
	public String getName() {
		return "REVERT";
	}

	@Override
	public void executeAction(MinigamePlayer player, Map<String, Object> args, Region region) {
		if(!player.isDead())
			Minigames.plugin.pdata.revertToCheckpoint(player);
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
