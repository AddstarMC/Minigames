package au.com.mineauz.minigames.minigame.reward.scheme;

import java.util.Map;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;

/**
 * RewardSchemes allow more flexibility for reward handling.
 * The previous simple Primary/Secondary reward system is under {@link StandardRewardScheme}
 */
public interface RewardScheme {
	/**
	 * Adds menu items to the /mg edit menu for this scheme. These are added under a sub menu
	 * @param menu The menu to add into.
	 */
	public void addMenuItems(Menu menu);
	
	/**
	 * Awards the player with the rewards specified in this scheme.
	 * @param player The player to be awarded
	 * @param minigame The minigame they were playing
	 * @param firstCompletion True if this is the first time they are completing the minigame
	 */
	public void awardPlayer(MinigamePlayer player, Minigame minigame, boolean firstCompletion);
	
	/**
	 * Awards the player with the rewards specified in this scheme.
	 * This may not do anything if no lose rewards are available by this scheme
	 * @param player The player to be awarded
	 * @param minigame The minigame they were playing
	 */
	public void awardPlayerOnLoss(MinigamePlayer player, Minigame minigame);
	
	/**
	 * @return Returns the map of flags used for saving and loading values
	 */
	public Map<String, Flag<?>> getFlags();
	
	/**
	 * @return Returns true if this scheme should be saved in a separate config file
	 */
	public boolean useSeparateConfig();
}
