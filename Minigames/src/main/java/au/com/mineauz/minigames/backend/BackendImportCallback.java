package au.com.mineauz.minigames.backend;

import java.util.UUID;

import au.com.mineauz.minigames.stats.StatFormat;

/**
 * This callback is used to take data from one backend and apply it to another backend.
 * <h1>Execution order:</h1>
 * You can expect that the methods called will be in the following order:
 * <ul>
 * <li>begin() 1 time</li>
 * <li>acceptPlayer() 0 or more times</li>
 * <li>acceptMinigame() 0 or more times</li>
 * <li>acceptStat() 0 or more times</li>
 * <li>acceptStatMetadata() 0 or more times</li>
 * <li>end() 1 time</li>
 * </ul>
 */
public interface BackendImportCallback {
    /**
     * Called to prepare the destination backend to receive the data.
     * Implementation Note: You can use this to clear the database to
     * accept new data
     */
    void begin();

    /**
     * Called to add a player to the destination backend.
     * It is up to implementers to how often to actually add the
     * players to the backend but this will be called upon reading
     * each player from the source backend.
     *
     * @param playerId    The UUID of the player
     * @param name        The user name of the player
     * @param displayName the display name of the player
     */
    void acceptPlayer(UUID playerId, String name, String displayName);

    /**
     * Called to add a minigame to the destination backend.
     * It is up to implementers to how often to actually add the
     * minigames to the backend but this will be called upon reading
     * each minigame from the source backend.
     *
     * @param id   The id of the minigame. This ID should stay the same but can be changed
     *             if you also change the if of corresponding data elsewhere
     * @param name The name of the minigame
     */
    void acceptMinigame(int id, String name);

    /**
     * Called to add a stat to the destination backend.
     * It is up to implementers to how often to actually add the
     * stats to the backend but this will be called upon reading
     * each stat from the source backend.
     *
     * @param playerId   The UUID of the owning player
     * @param minigameId The id of the minigame
     * @param stat       The name of the stat
     * @param value      The value of the stat
     */
    void acceptStat(UUID playerId, int minigameId, String stat, long value);

    /**
     * Called to add stat metadata to the destination backend.
     * It is up to implementers to how often to actually add the
     * metadata to the backend but this will be called upon reading
     * each stat metadata from the source backend.
     *
     * @param minigameId  The id of the minigame
     * @param stat        The stat name
     * @param displayName The stat display name
     * @param format      The stat format
     */
    void acceptStatMetadata(int minigameId, String stat, String displayName, StatFormat format);

    /**
     * Called to notify the destination backend that the source is finished.
     */
    void end();
}
