package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTime;
import com.pauldavdesign.mineauz.minigamesregions.Main;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.NodeTrigger;
import com.pauldavdesign.mineauz.minigamesregions.Region;
import com.pauldavdesign.mineauz.minigamesregions.RegionTrigger;

public class TimerAction implements ActionInterface {

	@Override
	public String getName() {
		return "TIMER";
	}

	@Override
	public String getCategory() {
		return "Remote Trigger Actions";
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
	public void executeRegionAction(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		final Region fregion = region;
		final MinigamePlayer fply = player;
		final Map<String, Object> fargs = args;
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){

			@Override
			public void run() {
				fregion.execute(RegionTrigger.TIMER, fply, null);
				fargs.put("a_timertask", -1);
			}
			
		}, 20 * (Integer)args.get("a_timer"));
		
		if((Integer)args.get("a_timertask") != -1)
			Bukkit.getServer().getScheduler().cancelTask((Integer)args.get("a_timertask"));
		args.put("a_timertask", taskId);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		final Node fnode = node;
		final MinigamePlayer fply = player;
		final Map<String, Object> fargs = args;
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				fnode.execute(NodeTrigger.TIMER, fply, null);
				fargs.put("a_timertask", -1);
			}
		}, 20 * (Integer)args.get("a_timer"));
		args.put("a_timertask", taskId);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_timer", 5);
		args.put("a_timertask", -1);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_timer", args.get("a_timer"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_timer", config.getInt(path + ".a_timer"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Timer", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemTime("Time Length", Material.WATCH, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_timer", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_timer");
			}
		}, 1, null));
		m.displayMenu(player);
		return true;
	}

}
