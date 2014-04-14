package com.pauldavdesign.mineauz.minigames.minigame.regions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public class HasRequiredFlagsCondition implements RegionConditionInterface {

	@Override
	public String getName() {
		return "HAS_REQUIRED_FLAGS";
	}

	@Override
	public boolean checkCondition(MinigamePlayer player,
			Map<String, Object> args) {
		if(Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()){
			if(!(Boolean)args.get("c_requiredflagsinvert"))
				return true;
		}
		else
			if((Boolean)args.get("c_requiredflagsinvert"))
				return true;
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_requiredflagsinvert", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_requiredflagsinvert", (Boolean)args.get("c_requiredflagsinvert"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_requiredflagsinvert", config.getBoolean(path + ".c_requiredflagsinvert"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			RegionExecutor exec) {
		Menu m = new Menu(3, "Required Flags", player);
		final RegionExecutor fexec = exec;
		m.addItem(new MenuItemBoolean("Invert Require Flags?", Material.ENDER_PEARL, new Callback<Boolean>() {
			
			@Override
			public void setValue(Boolean value) {
				fexec.getArguments().put("c_requiredflagsinvert", value);
			}
			
			@Override
			public Boolean getValue() {
				return (Boolean) fexec.getArguments().get("c_requiredflagsinvert");
			}
		}));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
