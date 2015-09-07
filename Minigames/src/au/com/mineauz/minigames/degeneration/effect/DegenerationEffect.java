package au.com.mineauz.minigames.degeneration.effect;

import org.bukkit.block.Block;

/**
 * Represents an effect for degeneration
 */
public interface DegenerationEffect {
	/**
	 * Gets the name of this effect
	 * @return The name
	 */
	public String getName();
	
	/**
	 * Gets a description of this effect for menus
	 * @return The description
	 */
	public String getDescription();
	
	/**
	 * Called to remove the block.
	 * Implementations must set the block to AIR 
	 * @param block The block to remove
	 */
	public void removeBlock(Block block);
}
