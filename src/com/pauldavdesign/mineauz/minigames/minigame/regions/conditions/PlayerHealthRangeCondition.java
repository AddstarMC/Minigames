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
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public class PlayerHealthRangeCondition implements RegionConditionInterface {

	@Override
	public String getName() {
		return "PLAYER_HEALTH_RANGE";
	}

	@Override
	public boolean checkCondition(MinigamePlayer player,
			Map<String, Object> args) {
		if(player.getPlayer().getHealth() >= (Double)args.get("c_phealthmin") &&
				player.getPlayer().getHealth() <= (Double)args.get("c_phealthmax"))
			return true;
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		
		args.put("c_phealthmin", 20.0);
		args.put("c_phealthmax", 20.0);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_phealthmin", args.get("c_phealthmin"));
		config.set(path + ".c_phealthmax", args.get("c_phealthmax"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_phealthmin", config.getDouble(path + ".c_phealthmin"));
		args.put("c_phealthmax", config.getDouble(path + ".c_phealthmax"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev, RegionExecutor exec) {
		Menu m = new Menu(3, "Health Range", player);
		final RegionExecutor fexec = exec;
		MenuItemInteger min = new MenuItemInteger("Min Health", Material.STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fexec.getArguments().put("c_phealthmin", (double)value);
			}
			
			@Override
			public Integer getValue() {
				Double d = (Double)fexec.getArguments().get("c_phealthmin");
				return d.intValue();
			}
		}, 0, 20);
		m.addItem(min);
		MenuItemInteger max = new MenuItemInteger("Max Health", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fexec.getArguments().put("c_phealthmax", (double)value);
			}
			
			@Override
			public Integer getValue() {
				Double d = (Double)fexec.getArguments().get("c_phealthmax");
				return d.intValue();
			}
		}, 0, 20);
		m.addItem(max);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
