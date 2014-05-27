package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class ExplodeAction implements ActionInterface {

	@Override
	public String getName() {
		return "EXPLODE";
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
		Random rand = new Random();
		double xrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockX() - region.getFirstPoint().getBlockX()) +
				region.getFirstPoint().getBlockX();
		double yrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockY() - region.getFirstPoint().getBlockY()) +
				region.getFirstPoint().getBlockY();
		double zrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockZ() - region.getFirstPoint().getBlockZ()) +
				region.getFirstPoint().getBlockZ();
		
		Location loc = region.getFirstPoint();
		loc.setX(xrand);
		loc.setY(yrand);
		loc.setZ(zrand);
		loc.getWorld().createExplosion(loc, (Float)args.get("a_explodepower"), (Boolean)args.get("a_explodefire"));
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		node.getLocation().getWorld().createExplosion(node.getLocation(), (Float)args.get("a_explodepower"), (Boolean)args.get("a_explodefire"));
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_explodepower", 4F);
		args.put("a_explodefire", false);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_explodepower", args.get("a_explodepower"));
		config.set(path + ".a_explodefire", args.get("a_explodefire"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_explodepower", ((Integer)config.getInt(path + ".a_explodepower")).floatValue());
		args.put("a_explodefire", config.getBoolean(path + ".a_explodefire"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Explode", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Explosion Power", Material.TNT, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_explodepower", value.floatValue());
			}
			
			@Override
			public Integer getValue() {
				return ((Float)fargs.get("a_explodepower")).intValue();
			}
		}, 0, 10));
		m.addItem(new MenuItemBoolean("Cause Fire", Material.FLINT_AND_STEEL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fargs.put("a_explodefire", value);
			}

			@Override
			public Boolean getValue() {
				return (Boolean)fargs.get("a_explodefire");
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
