package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class FlightAction extends ActionInterface{
	
	private final BooleanProperty setFly = new BooleanProperty(true, "setFlying");
	private final BooleanProperty startFly = new BooleanProperty(false, "startFly");
	
	public FlightAction() {
		properties.addProperty(setFly);
		properties.addProperty(startFly);
	}

	@Override
	public String getName() {
		return "FLIGHT";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		player.setCanFly(setFly.getValue());
		if (setFly.getValue()) {
			player.getPlayer().setFlying(startFly.getValue());
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Flight");
		m.addItem(new MenuItemBoolean("Set Flight Mode", Material.FEATHER, setFly));
		m.addItem(new MenuItemBoolean("Set Flying", "Set Flight Mode must be;true to use this", Material.FEATHER, startFly));
		m.displayMenu(player);
		return true;
	}

}
