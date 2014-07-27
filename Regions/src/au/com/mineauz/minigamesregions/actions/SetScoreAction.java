package au.com.mineauz.minigamesregions.actions;

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

public class SetScoreAction implements ActionInterface {

	@Override
	public String getName() {
		return "SET_SCORE";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
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
		if(player == null || !player.isInMinigame()) return;
		player.setScore((Integer)args.get("a_setscoreamount"));
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return;
		player.setScore((Integer)args.get("a_setscoreamount"));
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setscoreamount", 1);
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_setscoreamount", args.get("a_setscoreamount"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setscoreamount", config.getInt(path + ".a_setscoreamount"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Set Score", player);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Set Score Amount", Material.ENDER_PEARL, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_setscoreamount", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_setscoreamount");
			}
		}, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
