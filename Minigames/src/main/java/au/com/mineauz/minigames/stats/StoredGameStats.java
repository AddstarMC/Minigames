package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StoredGameStats {
    private final MinigamePlayer player;
    private final Minigame minigame;
    private final Map<MinigameStat, Long> stats;
    private final Map<MinigameStat, StatSettings> settings;

    public StoredGameStats(Minigame minigame, MinigamePlayer player) {
        this.minigame = minigame;
        this.player = player;

        stats = new HashMap<>();
        settings = new HashMap<>();
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
        Map<MinigameStat, Long> newStats = new HashMap<>(stats.size());
        newStats.putAll(stats);

        return Collections.unmodifiableMap(newStats);
    }

    public boolean hasStat(MinigameStat stat) {
        return stats.containsKey(stat);
    }

    public long getStat(MinigameStat stat) {
        Long value = stats.get(stat);
        return Objects.requireNonNullElse(value, 0L);
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
