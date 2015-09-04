package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class PlayerScoreRangeCondition extends ConditionInterface {
	
	private final IntegerProperty min = new IntegerProperty(5, "min");
	private final IntegerProperty max = new IntegerProperty(10, "max");
	
	public PlayerScoreRangeCondition() {
		properties.addProperty(min);
		properties.addProperty(max);
	}

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
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if(player.getScore() >= min.getValue() && player.getScore() <= max.getValue())
			return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Score Range");
		m.addItem(new MenuItemInteger("Min Score", Material.STEP, min, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemInteger("Max Score", Material.STONE, max, 0, Integer.MAX_VALUE));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
