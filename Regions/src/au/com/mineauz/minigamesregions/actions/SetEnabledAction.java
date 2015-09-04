package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetEnabledAction extends ActionInterface{
	
	private final BooleanProperty state = new BooleanProperty(false, "state");
	
	public SetEnabledAction() {
		properties.addProperty(state);
	}

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
		region.setEnabled(state.getValue());
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		node.setEnabled(state.getValue());
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Enabled");
		m.addItem(new MenuItemBoolean("Set Enabled", Material.ENDER_PEARL, state));
		m.displayMenu(player);
		return true;
	}

}
