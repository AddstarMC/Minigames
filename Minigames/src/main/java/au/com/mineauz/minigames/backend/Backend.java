package au.com.mineauz.minigames.backend;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatSettings;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public abstract class Backend {
    /**
     * Initializes the backend. This may include creating / converting tables as needed
     *
     * @param config The configuration to load settings from
     * @return Returns true if the initialization succeeded
     */
    public abstract boolean initialize(ConfigurationSection config, boolean debug);

    /**
     * Shutsdown the backend cleaning up resources
     */
    public abstract void shutdown();

    /**
     * Cleans unused connections
     */
    public abstract void clean();

    /**
     * Saves the game stats to the backend. This method is blocking.
     *
     * @param stats The game stats to store
     */
    public abstract void saveGameStatus(StoredGameStats stats);

    /**
     * Loads all player stats from the backend. This method is blocking.
     *
     * @param minigame The minigame to load stats for
     * @param stat     The stat to load
     * @param field    The field to load
     * @param order    The order to get the stats in
     * @return A list of stats matching the requirements
     */
    public abstract List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order);

    /**
     * Loads player stats from the backend. This method is blocking.
     *
     * @param minigame The minigame to load stats for
     * @param stat     The stat to load
     * @param field    The field to load
     * @param order    The order to get the stats in
     * @param offset   the starting index to load from
     * @param length   the maximum amount of data to return
     * @return A list of stats matching the requirements
     */
    public abstract List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length);

    /**
     * Gets the value of a stat for a player. This method is blocking
     *
     * @param minigame The minigame that value should be for
     * @param playerId the UUID of the player in question
     * @param stat     the stat to load
     * @param field    the field of the stat to load
     * @return The value of the stat
     */
    public abstract long getStat(Minigame minigame, UUID playerId, MinigameStat stat, StatValueField field);

    /**
     * Loads stat settings for the minigame
     *
     * @param minigame The minigame to load settings from
     * @return A map of stats to their settings
     */
    public abstract Map<MinigameStat, StatSettings> loadStatSettings(Minigame minigame);

    /**
     * Saves the stat settings for the minigame
     *
     * @param minigame The minigame to save settings for
     * @param settings The settings to save
     */
    public abstract void saveStatSettings(Minigame minigame, Collection<StatSettings> settings);

    /**
     * Exports this backend to another backend
     *
     * @param other    The backend to export to
     * @param notifier A callback to receive progress updates
     */
    public abstract void exportTo(Backend other, ExportNotifier notifier);

    protected abstract BackendImportCallback getImportCallback();

    protected final BackendImportCallback getImportCallback(Backend other) {
        return other.getImportCallback();
    }

    /**
     * Performs a conversion from a previous format
     *
     * @param notifier A notifier for progress updates
     * @returns True if the conversion succeeded
     */
    public abstract boolean doConversion(ExportNotifier notifier);
}
