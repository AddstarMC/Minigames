package au.com.mineauz.minigames.stats;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.minigame.Minigame;

/**
 * Represents all the players stats for a game including min, max, total, etc.
 */
public class StoredHistoryStats {
	private final UUID playerId;
	private final String playerName;
	private final String playerDispName;
	
	private final Minigame minigame;
	
	private Map<MinigameStat, Map<StatValueField, Long>> stats;
	
	public StoredHistoryStats(UUID playerId, String playerName, String playerDispName, Minigame minigame) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.playerDispName = playerDispName;
		this.minigame = minigame;
		
		stats = Maps.newHashMap();
	}
	
	/**
	 * @return Returns the UUID of the player owning these stats
	 */
	public UUID getPlayerId() {
		return playerId;
	}
	
	/**
	 * @return Returns the players user name
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	/**
	 * @return Returns the players display name
	 */
	public String getPlayerDisplayName() {
		return playerDispName;
	}
	
	/**
	 * @return Returns the minigame these stats are for
	 */
	public Minigame getMinigame() {
		return minigame;
	}
	
	/**
	 * Adds a stat value to this history
	 * @param stat The stat the value is for
	 * @param field The field the value is of
	 * @param value The value being stored
	 */
	public void addStat(MinigameStat stat, StatValueField field, long value) {
		Map<StatValueField, Long> fields = stats.get(stat);
		
		if (fields == null) {
			fields = Maps.newHashMap();
			stats.put(stat, fields);
		}
		
		fields.put(field, value);
	}
	
	/**
	 * Gets all values for the specified stat
	 * @param stat The stat to get values for
	 * @return A map of each field to the value it holds or an empty map
	 */
	public Map<StatValueField, Long> getValues(MinigameStat stat) {
		Map<StatValueField, Long> map = stats.get(stat);
		if (map != null) {
			return map;
		} else {
			return Collections.emptyMap();
		}
	}
	
	/**
	 * @return Returns a set of all stats in this history
	 */
	public Set<MinigameStat> getAvailbleStats() {
		return stats.keySet();
	}
	
	/**
	 * Gets the value of a field in a stat
	 * @param stat The stat containing the field
	 * @param field The field to use
	 * @return The value of the stat or 0
	 */
	public long getStat(MinigameStat stat, StatValueField field) {
		Map<StatValueField, Long> fields = getValues(stat);
		Long value = fields.get(field);
		if (value != null) {
			return value;
		} else {
			return 0;
		}
	}
}
