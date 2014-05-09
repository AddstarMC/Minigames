package com.pauldavdesign.mineauz.minigames.minigame.regions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.nodes.Node;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class ContainsOneTeamCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "CONTAINS_ONE_TEAM";
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
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node) {
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region) {
		boolean ret = true;
		Team last = player.getTeam();
		if(last == null) return true;
		for(MinigamePlayer p : region.getPlayers()){
			if(last != p.getTeam()){
				ret = false;
				break;
			}
		}
		if(ret && (Boolean)args.get("c_containsoneteaminvert"))
			return false;
		return ret;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_containsoneteaminvert", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_containsoneteaminvert", args.get("c_containsoneteaminvert"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_containsoneteaminvert", config.getBoolean(path + ".c_containsoneteaminvert"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Contains One Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemBoolean("Invert", Material.ENDER_PEARL, new Callback<Boolean>() {
			
			@Override
			public void setValue(Boolean value) {
				fargs.put("c_containsoneteaminvert", value);
			}
			
			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("c_containsoneteaminvert");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
