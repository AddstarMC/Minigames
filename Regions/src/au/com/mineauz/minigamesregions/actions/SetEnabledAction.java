package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetEnabledAction extends ActionInterface{
	
	private BooleanFlag state = new BooleanFlag(false, "state");

	@Override
	public String getName() {
		return "SET_ENABLED";
	}

	@Override
	public String getCategory() {
		return "Region/Node Actions";
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		region.setEnabled(state.getFlag());
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		node.setEnabled(state.getFlag());
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		state.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		state.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Enabled");
		m.addItem(state.getMenuItem("Set Enabled", Material.ENDER_PEARL));
		m.displayMenu(player);
		return true;
	}

}
