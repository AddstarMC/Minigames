package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class MessageAction implements ActionInterface {

	@Override
	public String getName() {
		return "MESSAGE";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
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
		if(args == null || !args.containsKey("message")) return;
		player.sendMessage((String)args.get("message"), null);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(args == null || !args.containsKey("message")) return;
		player.sendMessage((String)args.get("message"), null);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("message", "Hello World!");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path) {
		config.set(path + ".message", args.get("message"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config, String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		if(config.contains(path + ".message"))
			args.put("message", config.getString(path + ".message"));
		else
			args.put("message", "");
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous) {
		Menu m = new Menu(3, "Options", player);
		m.setPreviousPage(previous);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemString("Message", Material.PAPER, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("message", value);
			}
			
			@Override
			public String getValue() {
				return (String) fargs.get("message");
			}
		}));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, m.getPreviousPage()), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}
}
