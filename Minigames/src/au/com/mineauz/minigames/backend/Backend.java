package au.com.mineauz.minigames.backend;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public interface Backend {
	/**
	 * Initializes the backend. This may include creating / converting tables as needed
	 * @param config The configuration to load settings from
	 * @return Returns true if the initialization succeeded
	 */
	public boolean initialize(ConfigurationSection config);
	
	/**
	 * Saves the game stats to the backend. This method is blocking.
	 * @param stats The game stats to store
	 */
	public void saveGameStatus(StoredGameStats stats);
	
	/**
	 * Loads all player stats from the backend. This method is blocking.
	 * @param minigame The minigame to load stats for
	 * @param stat The stat to load
	 * @param field The field to load
	 * @param order The order to get the stats in
	 * @return A list of stats matching the requirements
	 */
	public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order);
	
	/**
	 * Loads player stats from the backend. This method is blocking.
	 * @param minigame The minigame to load stats for
	 * @param stat The stat to load
	 * @param field The field to load
	 * @param order The order to get the stats in
	 * @param offset the starting index to load from
	 * @param length the maximum amount of data to return
	 * @return A list of stats matching the requirements
	 */
	public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length);
	
	/**
	 * Gets the value of a stat for a player. This method is blocking
	 * @param minigame The minigame that value should be for
	 * @param playerId the UUID of the player in question
	 * @param stat the stat to load
	 * @param field the field of the stat to load
	 * @return The value of the stat
	 */
	public long getStat(Minigame minigame, UUID playerId, MinigameStat stat, StatValueField field);
}
