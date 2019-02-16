package au.com.mineauz.minigames.minigame.reward.scheme;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.StoredGameStats;

/**
 * RewardSchemes allow more flexibility for reward handling.
 * The previous simple Primary/Secondary reward system is under {@link StandardRewardScheme}
 */
public interface RewardScheme {
    /**
     * Adds menu items to the /mg edit menu for this scheme. These are added under a sub menu
     *
     * @param menu The menu to add into.
     */
    void addMenuItems(Menu menu);

    /**
     * Awards the player with the rewards specified in this scheme.
     *
     * @param player          The player to be awarded. <b>NOTE:</b> None of the stats will be set at this point. Use {@code data} to get that info
     * @param data            The SQLData for the minigame.
     * @param minigame        The minigame they were playing
     * @param firstCompletion True if this is the first time they are completing the minigame
     */
    void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion);

    /**
     * Awards the player with the rewards specified in this scheme.
     * This may not do anything if no lose rewards are available by this scheme
     *
     * @param player   The player to be awarded. <b>NOTE:</b> None of the stats will be set at this point. Use {@code data} to get that info
     * @param data     The SQLData for the minigame.
     * @param minigame The minigame they were playing
     */
    void awardPlayerOnLoss(MinigamePlayer player, StoredGameStats data, Minigame minigame);

    /**
     * @return Returns the map of flags used for saving and loading values
     */
    Map<String, Flag<?>> getFlags();

    /**
     * Saves any extra info for this scheme. Flags will be saved elsewhere
     *
     * @param config The config to write into
     */
    void save(ConfigurationSection config);

    /**
     * Loads any extra info for this scheme. Flags will be loaded elsewhere
     *
     * @param config The config to read from
     */
    void load(ConfigurationSection config);
}
