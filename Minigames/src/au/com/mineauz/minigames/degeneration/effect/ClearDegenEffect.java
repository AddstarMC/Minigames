package au.com.mineauz.minigames.degeneration.effect;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class ClearDegenEffect implements DegenerationEffect {
	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getDescription() {
		return "Removes the blocks with no effect";
	}

	@Override
	public void removeBlock(Block block) {
		block.setType(Material.AIR);
	}
}
