package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHealthRangeCondition extends ConditionInterface {
	
	private final IntegerProperty minHealth = new IntegerProperty(20, "min");
	private final IntegerProperty maxHealth = new IntegerProperty(20, "max");
	
	public PlayerHealthRangeCondition() {
		properties.addProperty(minHealth);
		properties.addProperty(maxHealth);
	}

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
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		if(player == null || !player.isInMinigame()) return false;
		if(player.getPlayer().getHealth() >= minHealth.getValue().doubleValue() &&
				player.getPlayer().getHealth() <= maxHealth.getValue().doubleValue())
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return false;
		if(player.getPlayer().getHealth() >= minHealth.getValue().doubleValue() &&
				player.getPlayer().getHealth() <= maxHealth.getValue().doubleValue())
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
		Menu m = new Menu(3, "Health Range");
		m.addItem(new MenuItemInteger("Min Health", Material.STEP, minHealth, 0, 20));
		m.addItem(new MenuItemInteger("Max Health", Material.STONE, maxHealth, 0, 20));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
