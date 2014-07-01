package au.com.mineauz.minigamesregions.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

public class RandomChanceCondition implements ConditionInterface {

	@Override
	public String getName() {
		return "RANDOM_CHANCE";
	}
	
	@Override
	public String getCategory(){
		return "Misc Conditions";
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
	public boolean checkRegionCondition(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		return check(args);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		return check(args);
	}
	
	private boolean check(Map<String, Object> args){
		double chance = (Integer) args.get("c_randomchance") / 100;
		Random rand = new Random();
		if(rand.nextDouble() <= chance)
			return true;
		return false;
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_randomchance", 50);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".c_randomchance", args.get("c_randomchance"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("c_randomchance", config.getDouble(path + ".c_randomchance"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev,
			Map<String, Object> args) {
		Menu m = new Menu(3, "Random Chance", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Percentage Chance", Material.EYE_OF_ENDER, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("c_randomchance", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer) fargs.get("c_randomchance");
			}
		}, 1, 99));
		return true;
	}

}
