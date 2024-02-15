package au.com.mineauz.minigames.stats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.UUID;

/**
 * Represents a stored stat for a player.
 * This is used for Scoreboards
 */
public final class StoredStat {
    private final UUID playerId;
    private final String playerName;
    private final Component playerDispName;

    private long value;

    public StoredStat(UUID playerId, String name, Component displayName, long value) {
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
    public Component getPlayerDisplayName() {
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
        return String.format("%s: %d", PlainTextComponentSerializer.plainText().serialize(playerDispName), value);
    }
}
