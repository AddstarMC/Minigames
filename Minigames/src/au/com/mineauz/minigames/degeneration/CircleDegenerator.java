package au.com.mineauz.minigames.degeneration;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.properties.types.BooleanProperty;

import com.google.common.collect.Lists;

public class CircleDegenerator extends Degenerator {
	private Location center;
	private int radius;
	
	public CircleDegenerator(Location min, Location max) {
		super(min, max);
		center = new Location(min.getWorld(), (min.getBlockX() + max.getBlockX())/2, (min.getBlockY() + max.getBlockY())/2, (min.getBlockZ() + max.getBlockZ())/2);
		radius = -2;
	}

	@Override
	public String getName() {
		return "circle";
	}

	@Override
	public String getDescription() {
		return "Removes blocks in a circle shape travelling from outside to inside";
	}

	private void addBlocks(int dx, int dz, List<Block> blocks) {
		int x = dx + center.getBlockX();
		int z = dz + center.getBlockZ();
		
		// Check that coords are in bounds
		if (x < min.getBlockX() || x > max.getBlockX() || z < min.getBlockZ() || z > max.getBlockZ()) {
			return;
		}
		
		// Add all y blocks
		for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
			blocks.add(center.getWorld().getBlockAt(x, y, z));
		}
	}
	
	@Override
	public Iterable<Block> next(DegeneratorSettings rawSettings) {
		if (radius == -2) {
			Settings settings = (Settings)rawSettings;
			if (settings.getUseFullArea()) {
				// Use the entire rectangle
				radius = (int)Math.max(max.distance(center), min.distance(center));
			} else {
				// Only touch the outermost edges, dont consume the corners
				int xMax = Math.max(max.getBlockX() - center.getBlockX(), center.getBlockX() - min.getBlockX());
				int zMax = Math.max(max.getBlockZ() - center.getBlockZ(), center.getBlockZ() - min.getBlockZ());
				
				radius = (int)Math.max(xMax, zMax);
			}
		}
		
		List<Block> blocks = Lists.newArrayList();
		
		int curRadius = radius * radius;
		int prevRadius = (radius+1) * (radius+1);
		
		for (int x = -radius - 1; x <= radius + 1; ++x) {
			for (int z = -radius - 1; z <= radius + 1; ++z) {
				// Get all blocks wihin the band between the current and previous radius
				if (x * x + z * z <= prevRadius && x * x + z * z >= curRadius) {
					addBlocks(x, z, blocks);
				}
			}
		}
		
		--radius;
		
		return blocks;
	}

	@Override
	public boolean isFinished() {
		return radius < 0 && radius != -2;
	}
	
	public static class Settings extends DegeneratorSettings {
		private final BooleanProperty fullArea = new BooleanProperty(false, "degen-circle-full");
		
		public Settings() {
			properties.addProperty(fullArea);
		}
		
		public boolean getUseFullArea() {
			return fullArea.getValue();
		}
		
		public void setUseFullArea(boolean use) {
			this.fullArea.setValue(use);
		}
		
		public Property<Boolean> useFullArea() {
			return fullArea;
		}
		
		@Override
		public void addMenuItems(Menu menu) {
			menu.addItem(new MenuItemBoolean("Use Full Area", "When on, the entire;area will be consumed;by the degenerator.;When off, the corners;will be missed", Material.LEVER, fullArea));
		}
	}

}
