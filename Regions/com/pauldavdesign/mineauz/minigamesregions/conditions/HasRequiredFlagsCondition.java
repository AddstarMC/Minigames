package com.pauldavdesign.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class HasRequiredFlagsCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "HAS_REQUIRED_FLAGS";
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
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
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
			Map<String, Object> args) {
		Menu m = new Menu(3, "Required Flags", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemBoolean("Invert Require Flags?", Material.ENDER_PEARL, new Callback<Boolean>() {
			
			@Override
			public void setValue(Boolean value) {
				fargs.put("c_requiredflagsinvert", value);
			}
			
			@Override
			public Boolean getValue() {
				return (Boolean) fargs.get("c_requiredflagsinvert");
			}
		}));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
