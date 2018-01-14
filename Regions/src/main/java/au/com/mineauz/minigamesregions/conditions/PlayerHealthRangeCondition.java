package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

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
	public void describe(Map<String, Object> out) {
		out.put("Health", minHealth.getFlag() + " - " + maxHealth.getFlag());
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
        return player != null && player.isInMinigame() && player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() && player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return player != null && player.isInMinigame() && player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() && player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }
	
	@Override
	public void saveArguments(FileConfiguration config, String path) {
		minHealth.saveValue(path, config);
		maxHealth.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		minHealth.loadValue(path, config);
		maxHealth.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Health Range", player);
		m.addItem(minHealth.getMenuItem("Min Health", Material.STEP, 0, 20));
		m.addItem(maxHealth.getMenuItem("Max Health", Material.STONE, 0, 20));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
