package com.pauldavdesign.mineauz.minigames.minigame.regions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public class PlayerScoreRangeCondition implements RegionConditionInterface {

	@Override
	public String getName() {
		return "PLAYER_SCORE_RANGE";
	}

	@Override
	public boolean checkCondition(MinigamePlayer player,
			Map<String, Object> args, Region region) {
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
			RegionExecutor exec) {
		Menu m = new Menu(3, "Score Range", player);
		final RegionExecutor fexec = exec;
		m.addItem(new MenuItemInteger("Min Score", Material.STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fexec.getArguments().put("c_playerscoremin", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fexec.getArguments().get("c_playerscoremin");
			}
		}, null, null));
		m.addItem(new MenuItemInteger("Max Score", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fexec.getArguments().put("c_playerscoremax", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fexec.getArguments().get("c_playerscoremax");
			}
		}, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
