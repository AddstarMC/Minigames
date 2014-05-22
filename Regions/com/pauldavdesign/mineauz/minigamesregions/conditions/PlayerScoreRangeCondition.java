package com.pauldavdesign.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class PlayerScoreRangeCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "PLAYER_SCORE_RANGE";
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
		if(player.getScore() >= (Integer)args.get("c_playerscoremin") && player.getScore() <= (Integer)args.get("c_playerscoremax"))
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		if(player.getScore() >= (Integer)args.get("c_playerscoremin") && player.getScore() <= (Integer)args.get("c_playerscoremax"))
			return true;
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playerscoremin", 5);
		args.put("c_playerscoremax", 10);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_playerscoremin", args.get("c_playerscoremin"));
		config.set(path + ".c_playerscoremax", args.get("c_playerscoremax"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playerscoremin", config.getInt(path + ".c_playerscoremin"));
		args.put("c_playerscoremax", config.getInt(path + ".c_playerscoremax"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Score Range", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Min Score", Material.STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_playerscoremin", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("c_playerscoremin");
			}
		}, null, null));
		m.addItem(new MenuItemInteger("Max Score", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_playerscoremax", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("c_playerscoremax");
			}
		}, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
