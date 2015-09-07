package au.com.mineauz.minigames.degeneration;

import java.util.Collections;
import java.util.List;

import org.bukkit.block.Block;

/**
 * Represents a degenerator that does nothing.
 */
class NullDegenerator extends Degenerator {
	public NullDegenerator() {
		super(null, null);
	}

	@Override
	public String getName() {
		return "NULL";
	}
	
	@Override
	public String getDescription() {
		return "NULL";
	}

	@Override
	public List<Block> next(DegeneratorSettings settings) {
		return Collections.emptyList();
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}
}
