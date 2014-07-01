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
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ContainsEntireTeamCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "CONTAINS_ENTIRE_TEAM";
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
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		if(region.getPlayers().containsAll(player.getTeam().getPlayers()))
			return true;
		return false;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_containsentireteam", "RED");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_containsentireteam", args.get("c_containsentireteam"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_containsentireteam", config.getString(path + ".c_containsentireteam"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Contains Entire Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		List<String> teams = new ArrayList<String>();
		for(TeamColor col : TeamColor.values())
			teams.add(MinigameUtils.capitalize(col.toString().replace("_", " ")));
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemList("Team", Material.LEATHER_CHESTPLATE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("c_containsentireteam", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(((String)fargs.get("c_containsentireteam")).replace("_", " "));
			}
		}, teams));
		m.displayMenu(player);
		return true;
	}

}
