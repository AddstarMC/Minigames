package au.com.mineauz.minigames.stats;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class StoredStats {
	private final MinigamePlayer player;
	private final Minigame minigame;
	private final Map<MinigameStat, Long> stats;
	
	public StoredStats(Minigame minigame, MinigamePlayer player) {
		this.minigame = minigame;
		this.player = player;
		
		stats = Maps.newHashMap();
	}
	
	public MinigamePlayer getPlayer() {
		return player;
	}
	
	public Minigame getMinigame() {
		return minigame;
	}
	
	public void addStat(MinigameStat stat, long value) {
		stats.put(stat, value);
	}
	
	public Map<MinigameStat, Long> getStats() {
		// Strip out the field based stats
		Map<MinigameStat, Long> newStats = Maps.newHashMapWithExpectedSize(stats.size());
		for (Entry<MinigameStat, Long> entry : stats.entrySet()) {
			if (entry.getKey() == MinigameStats.Wins ||
				entry.getKey() == MinigameStats.Attempts ||
				entry.getKey() == MinigameStats.CompletionTime) {
				continue;
			}
			
			newStats.put(entry.getKey(), entry.getValue());
		}
		
		return Collections.unmodifiableMap(newStats);
	}
	
	public long getStat(MinigameStat stat) {
		Long value = stats.get(stat);
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s in %s", player.getName(), minigame.getName(false));
	}
}
