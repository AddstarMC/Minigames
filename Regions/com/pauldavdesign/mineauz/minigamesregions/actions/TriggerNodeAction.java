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
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.NodeTrigger;
import com.pauldavdesign.mineauz.minigamesregions.Region;
import com.pauldavdesign.mineauz.minigamesregions.RegionModule;

public class TriggerNodeAction implements ActionInterface {

	@Override
	public String getName() {
		return "TRIGGER_NODE";
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
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = RegionModule.getMinigameModule(mg);
			if(rmod.hasNode((String)args.get("a_triggernode")))
				rmod.getNode((String)args.get("a_triggernode")).execute(NodeTrigger.REMOTE, player, null);
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Map<String, Object> args,
			Node node, Event event) {
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = RegionModule.getMinigameModule(mg);
			if(rmod.hasNode((String)args.get("a_triggernode")))
				rmod.getNode((String)args.get("a_triggernode")).execute(NodeTrigger.REMOTE, player, null);
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_triggernode", "none");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_triggernode", args.get("a_triggernode"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_triggernode", config.getString(path + ".a_triggernode"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Trigger Node", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.addItem(new MenuItemString("Node Name", Material.EYE_OF_ENDER, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("a_triggernode", value);
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("a_triggernode");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
