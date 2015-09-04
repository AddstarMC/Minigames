package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class AddScoreAction extends ActionInterface {
	
	private final IntegerProperty amount = new IntegerProperty(1, "amount");
	
	public AddScoreAction() {
		properties.addProperty(amount);
	}

	@Override
	public String getName() {
		return "ADD_SCORE";
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
			Node node) {
		if(player == null || !player.isInMinigame()) return;
		player.addScore(amount.getValue());
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return;
		player.addScore(amount.getValue());
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		amount.save(config.createSection(path));
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		amount.load(config.getConfigurationSection(path));
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Add Score");
		m.addItem(new MenuItemInteger("Add Score Amount", Material.ENDER_PEARL, amount, Integer.MIN_VALUE, Integer.MAX_VALUE));
		m.displayMenu(player);
		return true;
	}

}
