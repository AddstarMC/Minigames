package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDecimal;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class PlaySoundAction implements ActionInterface {

	@Override
	public String getName() {
		return "PLAY_SOUND";
	}

	@Override
	public String getCategory() {
		return "World Actions";
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
		execute(player, player.getLocation(), args);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		execute(player, node.getLocation(), args);
	}
	
	private void execute(MinigamePlayer player, Location loc, Map<String, Object> args){
		if((Boolean)args.get("a_playsoundprivate"))
			player.getPlayer().playSound(loc, 
					Sound.valueOf((String)args.get("a_playsound")), 
					(Float)args.get("a_playsoundvolume"), 
					(Float)args.get("a_playsoundpitch"));
		else
			player.getPlayer().getWorld().playSound(loc, 
					Sound.valueOf((String)args.get("a_playsound")), 
					(Float)args.get("a_playsoundvolume"), 
					(Float)args.get("a_playsoundpitch"));
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_playsound", "LEVEL_UP");
		args.put("a_playsoundprivate", false);
		args.put("a_playsoundvolume", 1f);
		args.put("a_playsoundpitch", 1f);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_playsound", args.get("a_playsound"));
		config.set(path + ".a_playsoundprivate", args.get("a_playsoundprivate"));
		config.set(path + ".a_playsoundvolume", args.get("a_playsoundvolume"));
		config.set(path + ".a_playsoundpitch", args.get("a_playsoundpitch"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_playsound", config.getString(path + ".a_playsound"));
		args.put("a_playsoundprivate", config.getBoolean(path + ".a_playsoundprivate"));
		args.put("a_playsoundvolume", Float.valueOf(config.getString(path + ".a_playsoundvolume")));
		args.put("a_playsoundpitch", Float.valueOf(config.getString(path + ".a_playsoundpitch")));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Play Sound", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		List<String> sounds = new ArrayList<String>();
		for(Sound s : Sound.values())
			sounds.add(MinigameUtils.capitalize(s.toString().replace("_", " ")));
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemList("Sound", Material.NOTE_BLOCK, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("a_playsound", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(((String)fargs.get("a_playsound")).replace("_", " "));
			}
		}, sounds));
		m.addItem(new MenuItemBoolean("Private Playback", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_playsoundprivate", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("a_playsoundprivate");
			}
		}));
		m.addItem(new MenuItemDecimal("Volume", Material.JUKEBOX, new Callback<Double>() {

			@Override
			public void setValue(Double value) {
				fargs.put("a_playsoundvolume", value.floatValue());
			}

			@Override
			public Double getValue() {
				return ((Float)fargs.get("a_playsoundvolume")).doubleValue();
			}
		}, 0.1, 1d, 0.5, null));
		m.addItem(new MenuItemDecimal("Pitch", Material.EYE_OF_ENDER, new Callback<Double>() {

			@Override
			public void setValue(Double value) {
				fargs.put("a_playsoundpitch", value.floatValue());
			}

			@Override
			public Double getValue() {
				return ((Float)fargs.get("a_playsoundpitch")).doubleValue();
			}
		}, 0.05, 0.1, 0d, 2d));
		m.displayMenu(player);
		return true;
	}

}
