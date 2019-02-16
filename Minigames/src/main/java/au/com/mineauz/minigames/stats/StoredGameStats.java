package au.com.mineauz.minigames.stats;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class StoredGameStats {
    private final MinigamePlayer player;
    private final Minigame minigame;
    private final Map<MinigameStat, Long> stats;
    private final Map<MinigameStat, StatSettings> settings;

    public StoredGameStats(Minigame minigame, MinigamePlayer player) {
        this.minigame = minigame;
        this.player = player;

        stats = Maps.newHashMap();
        settings = Maps.newHashMap();
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
        Map<MinigameStat, Long> newStats = Maps.newHashMapWithExpectedSize(stats.size());
        for (Entry<MinigameStat, Long> entry : stats.entrySet()) {
            newStats.put(entry.getKey(), entry.getValue());
        }

        return Collections.unmodifiableMap(newStats);
    }

    public boolean hasStat(MinigameStat stat) {
        return stats.containsKey(stat);
    }

    public long getStat(MinigameStat stat) {
        Long value = stats.get(stat);
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    public void applySettings(Map<MinigameStat, StatSettings> settings) {
        this.settings.putAll(settings);
    }

    public StatFormat getFormat(MinigameStat stat) {
        return settings.get(stat).getFormat();
    }

    @Override
    public String toString() {
        return String.format("%s in %s", player.getName(), minigame.getName(false));
    }
}
