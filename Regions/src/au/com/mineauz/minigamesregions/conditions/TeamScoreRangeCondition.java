package au.com.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class TeamScoreRangeCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "TEAM_SCORE_RANGE";
	}

	@Override
	public String getCategory() {
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
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		return checkCondition(player, args);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return checkCondition(player, args);
	}
	
	private boolean checkCondition(MinigamePlayer player, Map<String, Object> args){
		if(player.getTeam() != null){
			Team t = player.getTeam();
			if(t.getScore() >= (int)args.get("c_teamscorerangelower") && 
					t.getScore() <= (int)args.get("c_teamscorerangeupper")){
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_teamscorerangelower", 5);
		args.put("c_teamscorerangeupper", 10);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_teamscorerangelower", args.get("c_teamscorerangelower"));
		config.set(path + ".c_teamscorerangeupper", args.get("c_teamscorerangeupper"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_teamscorerangelower", config.getInt(path + ".c_teamscorerangelower"));
		args.put("c_teamscorerangeupper", config.getInt(path + ".c_teamscorerangeupper"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Team Score Range", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Minimum Score", Material.STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_teamscorerangelower", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer) fargs.get("c_teamscorerangelower");
			}
		}, 0, null));
		m.addItem(new MenuItemInteger("Maximum Score", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_teamscorerangeupper", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer) fargs.get("c_teamscorerangeupper");
			}
		}, 0, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
