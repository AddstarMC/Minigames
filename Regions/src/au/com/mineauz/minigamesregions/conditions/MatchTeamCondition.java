package au.com.mineauz.minigamesregions.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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
		boolean inv = (Boolean)args.get("c_matchteaminvert");
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(args.get("c_matchteam"))){
			if(!inv)
				return true;
			else
				return false;
		}
		if(inv)
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		boolean inv = (Boolean)args.get("c_matchteaminvert");
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(args.get("c_matchteam"))){
			if(!inv)
				return true;
			else
				return false;
		}
		if(inv)
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
		args.put("c_matchteaminvert", config.getBoolean(path + ".c_matchteaminvert"));
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
				return MinigameUtils.capitalize(((String)fargs.get("c_matchteam")).replace("_", " "));
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
