package com.pauldavdesign.mineauz.minigamesregions.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class MatchTeamCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "MATCH_TEAM";
	}
	
	@Override
	public String getCategory(){
		return "Team Conditions";
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
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(args.get("c_matchteam")))
			if(!(Boolean)args.get("c_matchteaminvert"))
				return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
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
			Map<String, Object> args) {
		Menu m = new Menu(3, "Match Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		List<String> teams = new ArrayList<String>();
		for(TeamColor t : TeamColor.values())
			teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
		m.addItem(new MenuItemList("Team Color", Material.WOOL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("c_matchteam", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("c_matchteam");
			}
		}, teams));
		m.addItem(new MenuItemBoolean("Invert Match", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("c_matchteaminvert", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("c_matchteaminvert");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
