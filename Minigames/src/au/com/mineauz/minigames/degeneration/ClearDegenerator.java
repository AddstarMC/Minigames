package au.com.mineauz.minigames.degeneration;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.collect.Lists;

public class ClearDegenerator extends Degenerator {
	private boolean done;
	public ClearDegenerator(Location min, Location max) {
		super(min, max);
	}
	
	@Override
	public String getName() {
		return "clear";
	}
	
	@Override
	public String getDescription() {
		return "Clears the entire area at once";
	}

	@Override
	public Iterable<Block> next(DegeneratorSettings settings) {
		List<Block> blocks = Lists.newArrayList();
			
		for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
					blocks.add(min.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		
		done = true;

		return blocks;
	}

	@Override
	public boolean isFinished() {
		return done;
	}

}
