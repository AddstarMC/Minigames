package au.com.mineauz.minigames.stats;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents per minigame settings for a stat
 */
public class StatSettings {
    private final @NotNull MinigameStat stat;
    private @Nullable StatFormat format;
    private @Nullable Component displayName;

    public StatSettings(@NotNull MinigameStat stat, @Nullable StatFormat format, @Nullable Component displayName) {
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
    public @NotNull MinigameStat getStat() {
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
    public void setFormat(@Nullable StatFormat format) {
        this.format = format;
    }

    /**
     * @return Returns the current display name of this stat
     */
    public Component getDisplayName() {
        if (displayName == null) {
            return stat.getDisplayName();
        } else {
            return displayName;
        }
    }

    /**
     * Sets the display name of this stat for this minigame
     *
     * @param displayName The new name of this stat. Setting to null will reset the name
     */
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }
}
