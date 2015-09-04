package au.com.mineauz.minigamesregions.actions;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

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
	public boolean requiresPlayer() {
		return false;
	}
	
	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		Location location;
		
		if (area instanceof Region) {
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
		} else if (area instanceof Node) {
			location = ((Node)area).getLocation();
		} else {
			return;
		}
		
		if(effect.getValue())
			location.getWorld().strikeLightningEffect(location);
		else
			location.getWorld().strikeLightning(location);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Lightning");
		m.addItem(new MenuItemBoolean("Effect Only", Material.ENDER_PEARL, effect));
		m.displayMenu(player);
		return true;
	}

}
