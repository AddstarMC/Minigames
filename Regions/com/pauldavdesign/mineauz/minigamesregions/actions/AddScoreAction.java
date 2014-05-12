package com.pauldavdesign.mineauz.minigamesregions.actions;

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

public class AddScoreAction implements ActionInterface {

	@Override
	public String getName() {
		return "ADD_SCORE";
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
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		player.addScore((Integer)args.get("a_addscoreamount"));
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(!player.isInMinigame()) return;
		player.addScore((Integer)args.get("a_addscoreamount"));
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_addscoreamount", 1);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_addscoreamount", args.get("a_addscoreamount"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_addscoreamount", config.getConfigurationSection(path + ".a_addscoreamount"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Add Score", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Add Score Amount", Material.ENDER_PEARL, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_addscoreamount", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_addscoreamount");
			}
		}, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
