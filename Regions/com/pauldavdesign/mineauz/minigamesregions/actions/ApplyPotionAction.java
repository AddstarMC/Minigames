package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTime;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class ApplyPotionAction implements ActionInterface {

	@Override
	public String getName() {
		return "APPLY_POTION";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
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
		execute(player, args);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		execute(player, args);
	}
	
	private void execute(MinigamePlayer player, Map<String, Object> args){
		PotionEffect effect = new PotionEffect(PotionEffectType.getByName((String)args.get("a_applypotiontype")), (Integer)args.get("a_applypotiondur") * 20, (Integer)args.get("a_applypotionamp") - 1);
		player.getPlayer().addPotionEffect(effect);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_applypotiontype", "SPEED");
		args.put("a_applypotiondur", 60);
		args.put("a_applypotionamp", 1);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_applypotiontype", args.get("a_applypotiontype"));
		config.set(path + ".a_applypotiondur", args.get("a_applypotiondur"));
		config.set(path + ".a_applypotionamp", args.get("a_applypotionamp"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_applypotiontype", config.getString(path + ".a_applypotiontype"));
		args.put("a_applypotiondur", config.getInt(path + ".a_applypotiondur"));
		args.put("a_applypotionamp", config.getInt(path + ".a_applypotionamp"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Apply Potion", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		List<String> pots = new ArrayList<String>(PotionEffectType.values().length);
		for(PotionEffectType type : PotionEffectType.values())
			pots.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		m.addItem(new MenuItemList("Potion Type", Material.POTION, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("a_applypotiontype", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(((String)fargs.get("a_applypotiontype")).replace("_", " "));
			}
		}, pots));
		m.addItem(new MenuItemTime("Duration", Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("a_applypotiondur", value);
			}

			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_applypotiondur");
			}
		}, 0, 86400));
		m.addItem(new MenuItemInteger("Level", Material.DOUBLE_STEP, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				fargs.put("a_applypotionamp", value);
			}

			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_applypotionamp");
			}
		}, 0, 100));
		m.displayMenu(player);
		return true;
	}

}
