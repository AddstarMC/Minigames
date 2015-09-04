package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.types.FloatProperty;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.TriggerArea;

public class VelocityAction extends ActionInterface{
	
	private final FloatProperty x = new FloatProperty(0f, "xv");
	private final FloatProperty y = new FloatProperty(5f, "yv");
	private final FloatProperty z = new FloatProperty(0f, "zv");
	
	public VelocityAction() {
		properties.addProperty(x);
		properties.addProperty(y);
		properties.addProperty(z);
	}

	@Override
	public String getName() {
		return "VELOCITY";
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
	public void executeAction(final MinigamePlayer player, TriggerArea area) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				player.getPlayer().setVelocity(new Vector(x.getValue(), y.getValue(), z.getValue()));
			}
		});
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Velocity");
		m.addItem(new MenuItemDecimal("X Velocity", Material.STONE, Properties.toDouble(x), 0.5d, 1d, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Y Velocity", Material.STONE, Properties.toDouble(y), 0.5d, 1d, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Z Velocity", Material.STONE, Properties.toDouble(z), 0.5d, 1d, Double.MIN_VALUE, Double.MAX_VALUE));
		m.displayMenu(player);
		return true;
	}

}
