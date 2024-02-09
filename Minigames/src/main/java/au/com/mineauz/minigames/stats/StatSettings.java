package au.com.mineauz.minigames.stats;

import net.kyori.adventure.text.Component;

import java.util.Objects;

/**
 * Represents per minigame settings for a stat
 */
public class StatSettings {
    private final MinigameStat stat;
    private StatFormat format;
    private Component displayName;

    public StatSettings(MinigameStat stat, StatFormat format, Component displayName) {
        this.stat = stat;
        this.format = format;
        this.displayName = displayName;
    }

    public StatSettings(MinigameStat stat) {
        this(stat, null, null);
    }

    /**
     * @return Returns the stat
     */
    public MinigameStat getStat() {
        return stat;
    }

    /**
     * @return Returns the current format of this stat for this minigame
     */
    public StatFormat getFormat() {
        if (format == null) {
            return stat.getFormat();
        } else {
            return format;
        }
    }

    /**
     * Sets the format of this stat for this minigame.
     *
     * @param format The new format to display. Setting to null will reset the format
     */
    public void setFormat(StatFormat format) {
        this.format = format;
    }

    /**
     * @return Returns the current display name of this stat
     */
    public Component getDisplayName() {
        return Objects.requireNonNullElseGet(displayName, stat::getDisplayName);
    }

    /**
     * Sets the display name of this stat for this minigame
     *
     * @param displayName The new name of this stat. Setting to null will reset the name
     */
    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }
}
