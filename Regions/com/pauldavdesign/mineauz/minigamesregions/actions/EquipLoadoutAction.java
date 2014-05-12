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
import com.pauldavdesign.mineauz.minigames.minigame.modules.LoadoutModule;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class EquipLoadoutAction implements ActionInterface {

	@Override
	public String getName() {
		return "EQUIP_LOADOUT";
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
		if(player.isInMinigame()){
			LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
			if(lmod.hasLoadout((String)args.get("a_equiploadout"))){
				player.setLoadout(lmod.getLoadout((String)args.get("a_equiploadout")));
			}
		}
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(player.isInMinigame()){
			LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
			if(lmod.hasLoadout((String)args.get("a_equiploadout"))){
				player.setLoadout(lmod.getLoadout((String)args.get("a_equiploadout")));
			}
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_equiploadout", "default");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_equiploadout", (String)args.get("a_equiploadout"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_equiploadout", config.getString(path + ".a_equiploadout"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Equip Loadout", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("a_equiploadout", value);
			}
			
			@Override
			public String getValue() {
				return (String)fargs.get("a_equiploadout");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
