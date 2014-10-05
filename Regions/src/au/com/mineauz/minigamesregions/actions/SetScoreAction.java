package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetScoreAction extends ActionInterface {
	
	private IntegerFlag amount = new IntegerFlag(1, "amount");

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
			Node node) {
		if(player == null || !player.isInMinigame()) return;
		player.setScore(amount.getFlag());
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return;
		player.setScore(amount.getFlag());
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		amount.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		amount.saveValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Score", player);
		m.addItem(amount.getMenuItem("Set Score Amount", Material.ENDER_PEARL, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
