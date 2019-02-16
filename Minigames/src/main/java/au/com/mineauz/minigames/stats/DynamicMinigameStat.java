package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

/**
 * DynamicMinigameStats allow you to add stats that are always checked on win or loss.
 * The values of these dynamic stats will be automatically computed and saved upon
 * game end.
 */
public abstract class DynamicMinigameStat extends MinigameStat {
    DynamicMinigameStat(String name, StatFormat format) {
        super(name, format);
    }

    /**
     * Checks if this stat applies to this minigame, player, and win condition
     *
     * @param minigame The minigame to check against
     * @param player   The player to check
     * @param win      True if the player won the game
     * @return True if this stat applies and should be used
     */
    public abstract boolean doesApply(Minigame minigame, MinigamePlayer player, boolean win);

    /**
     * Gets the value of this stat so it can be saved.
     *
     * @param minigame The minigame the player is in
     * @param player   The player
     * @param win      True if the player won the game
     * @return The value of this stat
     */
    public abstract long getValue(Minigame minigame, MinigamePlayer player, boolean win);
}
