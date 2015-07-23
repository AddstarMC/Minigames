package au.com.mineauz.minigames.stats;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * This class allows you to register stats that are usable in scoreboards 
 */
public final class MinigameStats {
	public static final MinigameStat Wins = new BasicMinigameStat("wins", StatFormat.Total, true);
	public static final MinigameStat Attempts = new BasicMinigameStat("attempts", StatFormat.Total, true);
	public static final MinigameStat CompletionTime = new BasicMinigameStat("time", StatFormat.MinMaxAndTotal, true);
	
	public static final MinigameStat Kills = new BasicMinigameStat("kills", StatFormat.MaxAndTotal);
	public static final MinigameStat Deaths = new BasicMinigameStat("deaths", StatFormat.MinAndTotal);
	public static final MinigameStat Score = new BasicMinigameStat("score", StatFormat.MaxAndTotal);
	public static final MinigameStat Reverts = new BasicMinigameStat("reverts", StatFormat.MinAndTotal);
	
	private static Map<String, MinigameStat> stats = Maps.newHashMap();
	
	static {
		registerStat0(Wins);
		registerStat0(Attempts);
		registerStat0(CompletionTime);
		registerStat0(Kills);
		registerStat0(Deaths);
		registerStat0(Score);
		registerStat0(Reverts);
	}
	
	/**
	 * Registers a new stat that is automatically saved and made available to scoreboards
	 * @param stat The stat to add. The name of the stat must be unique and must only contain only letters and numbers
	 * @throws IllegalArgumentException Thrown if the stat name is not unique or contains invalid characters
	 */
	public static void registerStat(DynamicMinigameStat stat) throws IllegalArgumentException {
		registerStat0(stat);
	}
	
	private static void registerStat0(MinigameStat stat) throws IllegalArgumentException {
		String name = stat.getName().toLowerCase();
		
		// Validity tests
		if (!isNameValid(name)) {
			throw new IllegalArgumentException("Invalid name '" + stat.getName() + "' for stat.");
		}
		
		if (stats.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate stat '" + stat.getName() + "'");
		}
		
		// Add the stat
		stats.put(name, stat);
	}
	
	private static boolean isNameValid(String name) {
		for (char c : name.toCharArray()) {
			if (!Character.isDigit(c) && !Character.isLetter(c)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets a stat by name
	 * @param name The name of the stat case insensitive
	 * @return The found stat or null
	 */
	public static MinigameStat getStat(String name) {
		return stats.get(name.toLowerCase());
	}
	
	/**
	 * Checks if a stat exists
	 * @param name The name of the stat case insensitive
	 * @return True if it exists
	 */
	public static boolean hasStat(String name) {
		return stats.containsKey(name.toLowerCase());
	}
	
	/**
	 * Removes a previously registered stat. This can remove 
	 * any stat added through {@link #registerStat(DynamicMinigameStat)}
	 * @param name The name of the stat case insensitive
	 * @return True if a stat was removed
	 */
	public static boolean removeStat(String name) {
		MinigameStat stat = stats.get(name.toLowerCase());
		
		if (stat instanceof DynamicMinigameStat) {
			return stats.remove(name.toLowerCase()) != null;
		} else {
			return false;
		}
	}
	
	/**
	 * @return Returns an unmodifiable map of all registered stats
	 */
	public static Map<String, MinigameStat> getAllStats() {
		return Collections.unmodifiableMap(stats);
	}
	
	/**
	 * @return Returns all dynamic stats
	 */
	public static Iterable<DynamicMinigameStat> getDynamicStats() {
		return Iterables.filter(stats.values(), DynamicMinigameStat.class);
	}
	
	private MinigameStats() {}

	
}
