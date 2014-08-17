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

public class PlayerHealthRangeCondition extends ConditionInterface {
	
	private IntegerFlag minHealth = new IntegerFlag(20, "min");
	private IntegerFlag maxHealth = new IntegerFlag(20, "max");

	@Override
	public String getName() {
		return "PLAYER_HEALTH_RANGE";
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
		if(player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
				player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue())
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		if(player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
				player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue())
			return true;
		return false;
	}
	
	@Override
	public void saveArguments(FileConfiguration config, String path) {
		minHealth.saveValue(path, config);
		maxHealth.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		minHealth.loadValue(path, config);
		maxHealth.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Health Range", player);
		m.addItem(minHealth.getMenuItem("Min Health", Material.STEP, 0, 20));
		m.addItem(maxHealth.getMenuItem("Max Health", Material.DOUBLE_STEP, 0, 20));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
