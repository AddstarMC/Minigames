package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MinigameTimerCondition extends ConditionInterface{
	
	private final IntegerProperty minTime = new IntegerProperty(5, "minTime");
	private final IntegerProperty maxTime = new IntegerProperty(10, "maxTime");
	
	public MinigameTimerCondition() {
		properties.addProperty(minTime);
		properties.addProperty(maxTime);
	}

	@Override
	public String getName() {
		return "MINIGAME_TIMER";
	}

	@Override
	public String getCategory() {
		return "Minigame Conditions";
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
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return check(player.getMinigame());
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check(player.getMinigame());
	}
	
	private boolean check(Minigame mg){
		if(mg.getMinigameTimer().getTimeLeft() >= minTime.getValue() &&
				mg.getMinigameTimer().getTimeLeft() <= maxTime.getValue()){
			return true;
		}
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
		Menu m = new Menu(3, "Minigame Timer");
		
		m.addItem(new MenuItemTime("Min Time", Material.WATCH, minTime, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemTime("Max Time", Material.WATCH, maxTime, 0, Integer.MAX_VALUE));

		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
