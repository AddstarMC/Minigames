package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.TriggerArea;

public class PulseRedstoneAction extends ActionInterface {
	
	private final IntegerProperty time = new IntegerProperty(1, "time");
	private final BooleanProperty torch = new BooleanProperty(false, "torch");
	
	public PulseRedstoneAction() {
		properties.addProperty(time);
		properties.addProperty(torch);
	}

	@Override
	public String getName() {
		return "PULSE_REDSTONE";
	}

	@Override
	public String getCategory() {
		return "Block Actions";
	}

	@Override
	public boolean useInRegions() {
		return false;
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
		if (area instanceof Node) {
			Node node = (Node)area;
			Material block = Material.REDSTONE_BLOCK;
			if(torch.getValue())
				block = Material.REDSTONE_TORCH_ON;
			final BlockState last = node.getLocation().getBlock().getState();
			node.getLocation().getBlock().setType(block);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
				@Override
				public void run() {
					last.update(true);
				}
			}, 20 * time.getValue());
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
		Menu m = new Menu(3, "Redstone Pulse");
		m.addItem(new MenuItemInteger("Pulse Time", Material.WATCH, time, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Use Redstone Torch", Material.REDSTONE_BLOCK, torch));
		m.displayMenu(player);
		return true;
	}

}
