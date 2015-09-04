package au.com.mineauz.minigamesregions.actions;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.FloatProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class ExplodeAction extends ActionInterface {
	
	private final FloatProperty power = new FloatProperty(4f, "power");
	private final BooleanProperty fire = new BooleanProperty(false, "fire");
	
	public ExplodeAction() {
		properties.addProperty(power);
		properties.addProperty(fire);
	}

	@Override
	public String getName() {
		return "EXPLODE";
	}

	@Override
	public String getCategory() {
		return "World Actions";
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
	public boolean requiresPlayer() {
		return false;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		Location location;
		if (area instanceof Node) {
			location = ((Node)area).getLocation();
		} else if (area instanceof Region) {
			Region region = (Region)area;
			Random rand = new Random();
			double xrand = rand.nextDouble() *
					(region.getMaxCorner().getBlockX() - region.getMinCorner().getBlockX()) +
					region.getMinCorner().getBlockX();
			double yrand = rand.nextDouble() *
					(region.getMaxCorner().getBlockY() - region.getMinCorner().getBlockY()) +
					region.getMinCorner().getBlockY();
			double zrand = rand.nextDouble() *
					(region.getMaxCorner().getBlockZ() - region.getMinCorner().getBlockZ()) +
					region.getMinCorner().getBlockZ();
			
			location = new Location(region.getWorld(), xrand, yrand, zrand);
		} else {
			return;
		}
		
		location.getWorld().createExplosion(location, power.getValue(), fire.getValue());
	}
	
	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Explode");
		m.addItem(new MenuItemDecimal("Explosion Power", Material.TNT, Properties.toDouble(power), 1, 1, 0, Double.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Cause Fire", Material.FLINT_AND_STEEL, fire));
		m.displayMenu(player);
		return true;
	}

}
