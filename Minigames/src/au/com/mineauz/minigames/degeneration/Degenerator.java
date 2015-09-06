package au.com.mineauz.minigames.degeneration;

import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class Degenerator {
	protected final Location min;
	protected final Location max;
	
	public Degenerator(Location min, Location max) {
		this.min = min;
		this.max = max;
	}
	
	public abstract String getName();
	
	public abstract Iterable<Block> next();
	public abstract boolean isFinished();
}
