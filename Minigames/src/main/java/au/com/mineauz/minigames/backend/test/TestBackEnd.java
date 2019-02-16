package au.com.mineauz.minigames.backend.test;

import java.util.*;

import au.com.mineauz.minigames.backend.Backend;
import au.com.mineauz.minigames.backend.BackendImportCallback;
import au.com.mineauz.minigames.backend.ExportNotifier;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created for the AddstarMC Project. Created by Narimm on 7/02/2019.
 */
public class TestBackEnd extends Backend {
    private List<StoredGameStats> playerGameStats = new ArrayList<>();
    private Map<Minigame, Collection<StatSettings>> gameSettings = new HashMap<>();


    /**
     * Initializes the backend. This may include creating / converting tables as needed
     *
     * @param config The configuration to load settings from
     * @param debug  if in debug mode
     * @return Returns true if the initialization succeeded
     */
    @Override
    public boolean initialize(ConfigurationSection config, boolean debug) {
        return true;
    }

    /**
     * Shutsdown the backend cleaning up resources
     */
    @Override
    public void shutdown() {
        playerGameStats.clear();
        gameSettings.clear();
    }

    /**
     * Cleans unused connections
     */
    @Override
    public void clean() {
    }

    /**
     * Saves the game stats to the backend. This method is blocking.
     *
     * @param stats The game stats to store
     */
    @Override
    public void saveGameStatus(StoredGameStats stats) {
        playerGameStats.add(stats);
        for (StoredGameStats stat : playerGameStats) {

        }
    }

    /**
     * Loads all player stats from the backend. This method is blocking.
     *
     * @param minigame The minigame to load stats for
     * @param stat     The stat to load
     * @param field    The field to load
     * @param order    The order to get the stats in
     * @return A list of stats matching the requirements
     */
    @Override
    public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order) {
        List<StoredStat> result = new ArrayList<>();
        for (StoredGameStats store : playerGameStats) {
            if (store.hasStat(stat) && store.getMinigame().getName(false).equals(minigame.getName(false))) {
                result.add(new StoredStat(store.getPlayer().getUUID(), store.getPlayer().getName(), store.getPlayer().getDisplayName(true), store.getStat(stat)));
            }
        }
        switch (order) {
            case DESCENDING:
                result.sort(Comparator.comparingLong(StoredStat::getValue).reversed());
                break;
            case ASCENDING:
            default:
                result.sort(Comparator.comparingLong(StoredStat::getValue));
        }
        return result;
    }


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
    @Override
    public List<StoredStat> loadStats(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length) {
        List<StoredStat> result = loadStats(minigame, stat, field, order);
        int i = offset;
        List<StoredStat> newR = new ArrayList<>();
        while (i < i + length) {
            newR.add(result.get(i));
            i++;
        }
        return newR;
    }

    /**
     * Gets the value of a stat for a player. This method is blocking
     *
     * @param minigame The minigame that value should be for
     * @param playerId the UUID of the player in question
     * @param stat     the stat to load
     * @param field    the field of the stat to load
     * @return The value of the stat
     */
    @Override
    public long getStat(Minigame minigame, UUID playerId, MinigameStat stat, StatValueField field) {
        List<StoredStat> set = loadStats(minigame, stat, field, ScoreboardOrder.ASCENDING);
        for (StoredStat s : set) {
            if (s.getPlayerId().equals(playerId)) {
                return s.getValue();
            }
        }
        return 0;
    }

    /**
     * Loads stat settings for the minigame
     *
     * @param minigame The minigame to load settings from
     * @return A map of stats to their settings
     */
    @Override
    public Map<MinigameStat, StatSettings> loadStatSettings(Minigame minigame) {
        Collection<StatSettings> statSettings = gameSettings.get(minigame);
        Map<MinigameStat, StatSettings> result = new HashMap<>();
        for (StatSettings s : statSettings) {
            result.put(s.getStat(), s);
        }
        return result;
    }

    /**
     * Saves the stat settings for the minigame
     *
     * @param minigame The minigame to save settings for
     * @param settings The settings to save
     */
    @Override
    public void saveStatSettings(Minigame minigame, Collection<StatSettings> settings) {
        gameSettings.put(minigame, settings);
    }

    /**
     * Exports this backend to another backend
     *
     * @param other    The backend to export to
     * @param notifier A callback to receive progress updates
     */
    @Override
    public void exportTo(Backend other, ExportNotifier notifier) {

    }

    @Override
    protected BackendImportCallback getImportCallback() {
        return null;
    }

    /**
     * Performs a conversion from a previous format
     *
     * @param notifier A notifier for progress updates
     * @returns True if the conversion succeeded
     */
    @Override
    public boolean doConversion(ExportNotifier notifier) {
        return true;
    }

}
