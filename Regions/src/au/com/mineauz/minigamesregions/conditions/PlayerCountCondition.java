package au.com.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerCountCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "PLAYER_COUNT";
	}

	@Override
	public String getCategory() {
		return "Player Conditions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		
		return false;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playercount", 1);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_playercount", args.get("c_playercount"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_playercount", config.getInt(path + ".c_playercount"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Player Count", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Player Count", Material.SKULL_ITEM, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_playercount", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("c_playercount");
			}
		}, 1, null));
		m.displayMenu(player);
		return true;
	}

}
