package au.com.mineauz.minigames.stats;

import java.util.UUID;

/**
 * Represents a stored stat for a player.
 * This is used for Scoreboards
 */
public final class StoredStat {
    private final UUID playerId;
    private final String playerName;
    private final String playerDispName;

    private long value;

    public StoredStat(UUID playerId, String name, String displayName, long value) {
        this.playerId = playerId;
        this.playerName = name;
        this.playerDispName = displayName;
        this.value = value;
    }

    /**
     * @return Returns the UUID of the owning player
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * @return Returns the name of the owning player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return Returns the display name of the owning player
     */
    public String getPlayerDisplayName() {
        return playerDispName;
    }

    /**
     * @return Returns the value for the stat
     */
    public long getValue() {
        return value;
    }

    /**
     * Sets the value of the stat
     *
     * @param value The value
     */
    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s: %d", playerDispName, value);
    }
}
