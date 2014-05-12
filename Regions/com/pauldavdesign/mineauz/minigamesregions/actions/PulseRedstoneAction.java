package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTime;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class PulseRedstoneAction implements ActionInterface {

	@Override
	public String getName() {
		return "PULSE_REDSTONE";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		Material block = Material.REDSTONE_BLOCK;
		if((Boolean) args.get("a_redstonepulsetorch"))
			block = Material.REDSTONE_TORCH_ON;
		final BlockState last = node.getLocation().getBlock().getState();
		node.getLocation().getBlock().setType(block);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				last.update(true);
			}
		}, 20 * (Integer)args.get("a_redstonepulsetime"));
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_redstonepulsetime", 1);
		args.put("a_redstonepulsetorch", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_redstonepulsetime", (Integer) args.get("a_redstonepulsetime"));
		config.set(path + ".a_redstonepulsetorch", (Boolean) args.get("a_redstonepulsetorch"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_redstonepulsetime", config.getInt(path + ".a_redstonepulsetime"));
		args.put("a_redstonepulsetorch", config.getBoolean(path + ".a_redstonepulsetorch"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Redstone Pulse", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemTime("Pulse Time", Material.WATCH, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_redstonepulsetime", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer) fargs.get("a_redstonepulsetime");
			}
		}, 1, null));
		m.addItem(new MenuItemBoolean("Use Redstone Torch", Material.REDSTONE_BLOCK, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_redstonepulsetorch", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean) fargs.get("a_redstonepulsetorch");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
