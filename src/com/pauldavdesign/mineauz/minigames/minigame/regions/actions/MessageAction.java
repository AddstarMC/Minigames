package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class MessageAction implements RegionActionInterface {

	@Override
	public String getName() {
		return "MESSAGE";
	}

	@Override
	public void executeAction(MinigamePlayer player, Map<String, Object> args, Region region) {
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
