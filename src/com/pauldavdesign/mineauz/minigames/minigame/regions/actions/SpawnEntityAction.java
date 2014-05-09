package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigames.minigame.nodes.Node;
import com.pauldavdesign.mineauz.minigames.minigame.regions.Region;

public class SpawnEntityAction implements ActionInterface {

	@Override
	public String getName() {
		return "SPAWN_ENTITY";
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
			Map<String, Object> args, Region region) {
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Map<String, Object> args,
			Node node) {
		Entity ent = node.getLocation().getWorld().spawnEntity(node.getLocation(), EntityType.valueOf((String)args.get("a_spawnentitytype")));
		ent.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.plugin, true));
		player.getMinigame().getBlockRecorder().addEntity(ent, player, true);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_spawnentitytype", "ZOMBIE");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_spawnentitytype", args.get("a_spawnentitytype"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_spawnentitytype", config.getString(path + ".a_spawnentitytype"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Spawn Entity", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		List<String> options = new ArrayList<String>();
		for(EntityType type : EntityType.values()){
			if(type != EntityType.ITEM_FRAME && type != EntityType.LEASH_HITCH && type != EntityType.PLAYER && 
					type != EntityType.COMPLEX_PART && type != EntityType.WEATHER && type != EntityType.LIGHTNING &&
					type != EntityType.PAINTING && type != EntityType.UNKNOWN)
				options.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		}
		m.addItem(new MenuItemList("Entity Type", Material.SKULL_ITEM, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				fargs.put("a_spawnentitytype", value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(((String) fargs.get("a_spawnentitytype")).replace("_", " "));
			}
		}, options));
		MenuItemCustom cus = new MenuItemCustom("Entity Settings", Material.CHEST);
		final Menu fm = m;
		final MinigamePlayer fplayer = player;
		final MenuItemCustom fcus = cus;
		cus.setClick(new InteractionInterface() {
			
			@Override
			public Object interact() {
				if(EntityType.valueOf((String)fargs.get("a_spawnentitytype")) == EntityType.ZOMBIE){
					menuMonster(fplayer, fargs, fm);
					return null;
				}
				return fcus.getItem();
			}
		});
		m.displayMenu(player);
		return true;
	}
	
	private void menuMonster(MinigamePlayer player, Map<String, Object> args,
			Menu previous){
		for(String arg : new ArrayList<String>(args.keySet())){
			if(arg.startsWith("a_spawnentity") && !arg.equals("a_spawnentitytype"))
				args.remove(arg);
		}
		args.put("a_spawnentitynamevisible", false);
		Menu m = new Menu(3, "Monsters", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous));
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemString("Custom Name", Material.NAME_TAG, new Callback<String>() {

			@Override
			public void setValue(String value) {
				fargs.put("a_spawnentitycustomname", value);
			}

			@Override
			public String getValue() {
				return (String)fargs.get("a_spawnentitycustomname");
			}
		}));
		m.addItem(new MenuItemBoolean("Name Always Visible", Material.NAME_TAG, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_spawnentitynamevisible", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("a_spawnentitynamevisible");
			}
		}));
	}
}
