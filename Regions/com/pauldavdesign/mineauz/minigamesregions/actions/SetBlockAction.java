package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class SetBlockAction implements ActionInterface {

	@Override
	public String getName() {
		return "SET_BLOCK";
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
		//TODO Region set block
	}

	@SuppressWarnings("deprecation")
	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		BlockState bs = node.getLocation().getBlock().getState();
		bs.setType(Material.getMaterial((String)args.get("a_setblocktype")));
		if((Boolean)args.get("a_setblockusedur")){
			bs.getData().setData((Byte)args.get("a_setblockdur"));
		}
		bs.update(true);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setblocktype", "AIR");
		args.put("a_setblockusedur", false);
		args.put("a_setblockdur", (byte)0);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_setblocktype", args.get("a_setblocktype"));
		config.set(path + ".a_setblockusedur", args.get("a_setblockusedur"));
		config.set(path + ".a_setblockdur", args.get("a_setblockdur"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setblocktype", config.getString(path + ".a_setblocktype"));
		args.put("a_setblockusedur", config.getBoolean(path + ".a_setblockusedur"));
		args.put("a_setblocktype", ((Integer)config.getInt(path + ".a_setblockdur")).byteValue());
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		return false;
	}

}
