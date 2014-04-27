package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class ReequipLoadoutAction implements RegionActionInterface {

	@Override
	public String getName() {
		return "REEQUIP_LOADOUT";
	}

	@Override
	public void executeAction(MinigamePlayer player, Map<String, Object> args, Region region) {
		if(player.isInMinigame())
			player.getLoadout().equiptLoadout(player);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		// None
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		// None
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		// None
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		// None
		return false;
	}

}
