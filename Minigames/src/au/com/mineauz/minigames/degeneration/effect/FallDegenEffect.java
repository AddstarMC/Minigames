package au.com.mineauz.minigames.degeneration.effect;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;

import au.com.mineauz.minigames.Minigames;

public class FallDegenEffect implements DegenerationEffect {
	@Override
	public String getName() {
		return "fall";
	}

	@Override
	public String getDescription() {
		return "Makes the blocks fall down like gravel";
	}

	@Override
	public void removeBlock(Block block) {
		FallingBlock ent = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
		ent.setDropItem(false);
		ent.setMetadata("MG|NOFORM", new FixedMetadataValue(Minigames.plugin, true));
		block.setType(Material.AIR);
	}
}
