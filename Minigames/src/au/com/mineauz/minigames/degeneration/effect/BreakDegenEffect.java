package au.com.mineauz.minigames.degeneration.effect;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BreakDegenEffect implements DegenerationEffect {
	@Override
	public String getName() {
		return "break";
	}

	@Override
	public String getDescription() {
		return "Breaks the block with the breaking particles";
	}

	@Override
	public void removeBlock(Block block) {
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
		block.setType(Material.AIR);
	}
}
