package au.com.mineauz.minigamesregions.actions;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class LightningAction extends ActionInterface{
	
	private final BooleanProperty effect = new BooleanProperty(false, "effect");
	
	public LightningAction() {
		properties.addProperty(effect);
	}

	@Override
	public String getName() {
		return "LIGHTNING";
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		Random rand = new Random();
		double xrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockX() - region.getFirstPoint().getBlockX()) +
				region.getFirstPoint().getBlockX();
		double yrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockY() - region.getFirstPoint().getBlockY()) +
				region.getFirstPoint().getBlockY();
		double zrand = rand.nextDouble() *
				(region.getSecondPoint().getBlockZ() - region.getFirstPoint().getBlockZ()) +
				region.getFirstPoint().getBlockZ();
		
		Location loc = region.getFirstPoint();
		loc.setX(xrand);
		loc.setY(yrand);
		loc.setZ(zrand);
		
		if(effect.getValue())
			loc.getWorld().strikeLightningEffect(loc);
		else
			loc.getWorld().strikeLightning(loc);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		if(effect.getValue())
			node.getLocation().getWorld().strikeLightningEffect(node.getLocation());
		else
			node.getLocation().getWorld().strikeLightning(node.getLocation());
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Lightning");
		m.addItem(new MenuItemBoolean("Effect Only", Material.ENDER_PEARL, effect));
		m.displayMenu(player);
		return true;
	}

}
