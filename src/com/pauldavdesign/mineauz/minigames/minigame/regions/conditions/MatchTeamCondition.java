package com.pauldavdesign.mineauz.minigames.minigame.regions.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public class MatchTeamCondition implements RegionConditionInterface {

	@Override
	public String getName() {
		return "MATCH_TEAM";
	}

	@Override
	public boolean checkCondition(MinigamePlayer player,
			Map<String, Object> args, Region region) {
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(args.get("c_matchteam")))
			if(!(Boolean)args.get("c_matchteaminvert"))
				return true;
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_matchteam", "RED");
		args.put("c_matchteaminvert", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_matchteam", args.get("c_matchteam"));
		config.set(path + ".c_matchteaminvert", args.get("c_matchteaminvert"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_matchteam", config.getString(path + ".c_matchteam"));
		args.put("c_matchteaminvert", config.getBoolean(path + ".matchteaminvert"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			RegionExecutor exec) {
		Menu m = new Menu(3, "Match Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final RegionExecutor fexec = exec;
		List<String> teams = new ArrayList<String>();
		for(TeamColor t : TeamColor.values())
			teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
		m.addItem(new MenuItemList("Team Color", Material.WOOL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fexec.getArguments().put("c_matchteam", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return (String)fexec.getArguments().get("c_matchteam");
			}
		}, teams));
		m.addItem(new MenuItemBoolean("Invert Match", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fexec.getArguments().put("c_matchteaminvert", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fexec.getArguments().get("c_matchteaminvert");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
