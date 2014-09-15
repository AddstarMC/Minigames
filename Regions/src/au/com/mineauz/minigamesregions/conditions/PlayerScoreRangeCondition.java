package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerScoreRangeCondition extends ConditionInterface {
	
	private IntegerFlag min = new IntegerFlag(5, "min");
	private IntegerFlag max = new IntegerFlag(10, "max");

	@Override
	public String getName() {
		return "PLAYER_SCORE_RANGE";
	}
	
	@Override
	public String getCategory(){
		return "Player Conditions";
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
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		if(player.getScore() >= min.getFlag() && player.getScore() <= max.getFlag())
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		if(player.getScore() >= min.getFlag() && player.getScore() <= max.getFlag())
			return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		min.saveValue(path, config);
		max.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		min.loadValue(path, config);
		max.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Score Range", player);
		m.addItem(min.getMenuItem("Min Score", Material.STEP));
		m.addItem(max.getMenuItem("Max Score", Material.DOUBLE_STEP));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
