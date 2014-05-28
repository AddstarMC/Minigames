package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class KillAction implements ActionInterface {

	@Override
	public String getName() {
		return "KILL";
	}

	@Override
	public String getCategory() {
		return "World Actions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		if(player == null || !player.isInMinigame()) return;
		if(!player.isDead())
			player.getPlayer().damage(player.getPlayer().getHealth());
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return;
		if(!player.isDead())
			player.getPlayer().damage(player.getPlayer().getHealth());
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
