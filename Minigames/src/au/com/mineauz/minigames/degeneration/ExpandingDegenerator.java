package au.com.mineauz.minigames.degeneration;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.collect.Sets;

abstract class ExpandingDegenerator extends Degenerator {
	private final boolean inward;
	
	private Location curMin;
	private Location curMax;
	
	private boolean done;
	
	public ExpandingDegenerator(Location min, Location max, boolean inward) {
		super(min, max);
		
		this.inward = inward;

		if (inward) {
			curMin = min.clone();
			curMax = max.clone();
		} else {
			// Find center
			int cx = (min.getBlockX() + max.getBlockX()) / 2;
			int cy = (min.getBlockY() + max.getBlockY()) / 2;
			int cz = (min.getBlockZ() + max.getBlockZ()) / 2;
			curMin = new Location(min.getWorld(), cx, cy, cz);
			curMax = curMin.clone();
		}
		
		done = false;
	}
	
	@Override
	public Iterable<Block> next() {
		Set<Block> blocks = Sets.newHashSet();
		
		boolean isXMin = (curMax.getBlockX() == curMin.getBlockX());
		boolean isYMin = (curMax.getBlockY() == curMin.getBlockY());
		boolean isZMin = (curMax.getBlockZ() == curMin.getBlockZ());
		
		// Add blocks
		for (int x = curMin.getBlockX(); x <= curMax.getBlockX(); ++x) {
			for (int y = curMin.getBlockY(); y <= curMax.getBlockY(); ++y) {
				for (int z = curMin.getBlockZ(); z <= curMax.getBlockZ(); ++z) {
					// Is on the edge
					if (((x == curMin.getBlockX() || x == curMax.getBlockX()) && !isXMin) ||
						((y == curMin.getBlockY() || y == curMax.getBlockY()) && !isYMin) ||
						((z == curMin.getBlockZ() || z == curMax.getBlockZ()) && !isZMin)) {
						blocks.add(min.getWorld().getBlockAt(x, y, z));
					// We only have a 1x1x1 cube
					} else if (isXMin && isYMin && isZMin) {
						blocks.add(min.getWorld().getBlockAt(x, y, z));
					}
				}
			}
		}
		
		if (inward) {
			contractBlocks();
		} else {
			expandBlocks();
		}
		
		return blocks;
	}
	
	private void contractBlocks() {
		boolean xDone = false;
		boolean yDone = false;
		boolean zDone = false;
		
		if (curMax.getBlockX() - curMin.getBlockX() > 2) {
			curMax.setX(curMax.getX() - 1);
			curMin.setX(curMin.getX() + 1);
		} else {
			int val = (curMax.getBlockX() + curMin.getBlockX()) / 2;
			curMax.setX(val);
			curMin.setX(val);
			xDone = true;
		}
		
		if (curMax.getBlockY() - curMin.getBlockY() > 2) {
			curMax.setY(curMax.getY() - 1);
			curMin.setY(curMin.getY() + 1);
		} else {
			int val = (curMax.getBlockY() + curMin.getBlockY()) / 2;
			curMax.setY(val);
			curMin.setY(val);
			yDone = true;
		}
		
		if (curMax.getBlockZ() - curMin.getBlockZ() > 2) {
			curMax.setZ(curMax.getZ() - 1);
			curMin.setZ(curMin.getZ() + 1);
		} else {
			int val = (curMax.getBlockZ() + curMin.getBlockZ()) / 2;
			curMax.setZ(val);
			curMin.setZ(val);
			zDone = true;
		}
		
		if (xDone && yDone && zDone) {
			done = true;
		}
	}
	
	private void expandBlocks() {
		boolean xMinDone = false;
		boolean xMaxDone = false;
		boolean yMinDone = false;
		boolean yMaxDone = false;
		boolean zMinDone = false;
		boolean zMaxDone = false;
		
		if (curMin.getBlockX() > min.getBlockX()) {
			curMin.setX(curMin.getX() - 1);
		} else {
			xMinDone = true;
		}
		if (curMax.getBlockX() < max.getBlockX()) {
			curMax.setX(curMax.getX() + 1);
		} else {
			xMaxDone = true;
		}
		
		if (curMin.getBlockY() > min.getBlockY()) {
			curMin.setY(curMin.getY() - 1);
		} else {
			yMinDone = true;
		}
		if (curMax.getBlockY() < max.getBlockY()) {
			curMax.setY(curMax.getY() + 1);
		} else {
			yMaxDone = true;
		}
		
		if (curMin.getBlockZ() > min.getBlockZ()) {
			curMin.setZ(curMin.getZ() - 1);
		} else {
			zMinDone = true;
		}
		if (curMax.getBlockZ() < max.getBlockZ()) {
			curMax.setZ(curMax.getZ() + 1);
		} else {
			zMaxDone = true;
		}
		
		if (xMinDone && xMaxDone && yMinDone && yMaxDone && zMinDone && zMaxDone) {
			done = true;
		}
	}

	@Override
	public boolean isFinished() {
		return done;
	}
	
	public static class InwardExpandingGenerator extends ExpandingDegenerator {
		public InwardExpandingGenerator(Location min, Location max) {
			super(min, max, true);
		}
		
		@Override
		public String getName() {
			return "inward";
		}
	}
	
	public static class OutwardExpandingGenerator extends ExpandingDegenerator {
		public OutwardExpandingGenerator(Location min, Location max) {
			super(min, max, false);
		}
		
		@Override
		public String getName() {
			return "outward";
		}
	}
}
