package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class ExecuteCommandAction implements ActionInterface {

	@Override
	public String getName() {
		return "EXECUTE_COMMAND";
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
		execute(args);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		execute(args);
	}
	
	private void execute(Map<String, Object> args){
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), (String) args.get("a_executecommand"));
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_executecommand", "say Hello!");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_executecommand", args.get("a_executecommand"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_executecommand", config.get(path + ".a_executecommand"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Execute Command", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemString("Command", MinigameUtils.stringToList("Do not include '/';If WorldEdit command, start with ./"), 
				Material.COMMAND, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(value.startsWith("./"))
					value = value.replaceFirst("./", "/");
				fargs.put("a_executecommand", value);
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("a_executecommand");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
