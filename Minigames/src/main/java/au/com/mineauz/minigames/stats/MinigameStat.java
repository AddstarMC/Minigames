package au.com.mineauz.minigames.stats;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class MinigameStat {
    private final @NotNull String name;
    private final @NotNull StatFormat format;
    private @Nullable Component displayName;

    MinigameStat(@NotNull String name, @Nullable Component displayName, @NotNull StatFormat format) {
        this.displayName = displayName;
        this.name = name;
        this.format = format;
    }

    MinigameStat(@NotNull String name, @NotNull StatFormat format) {
        this.name = name;
        this.format = format;
    }

    /**
     * @return Returns the name of this stat
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return Returns the general format of this stat.
     */
    public @NotNull StatFormat getFormat() {
        return format;
    }

    /**
     * @return Returns the current name for display purposes.
     * This will be the display name if set, otherwise it will be the actual name
     */
    public Component getDisplayName() {
        return Objects.requireNonNullElseGet(displayName, () -> Component.text(name));
    }

    /**
     * Sets the name that is displayed for this stat.
     *
     * @param name The new name or null to reset
     */
    public void setDisplayName(@Nullable Component name) {
        displayName = name;
    }

    /**
     * Queries this stat to check if its value should actually be stored.
     * The normal behaviour is to always save the stat unless the value
     * is 0 and the format doesn't contain the minimum or last value
     *
     * @param value        The value of this stat
     * @param actualFormat The format being saved. This will differ from the
     *                     format returned by {@link #getFormat()} when overridden by
     *                     specific minigames
     * @return True if the value should be saved
     */
    public boolean shouldStoreStat(long value, StatFormat actualFormat) {
        if (value == 0) {
            return switch (actualFormat) {
                case Last, LastAndTotal, Min, MinAndTotal, MinMax, MinMaxAndTotal -> true;
                default -> false;
            };
        } else {
            return true;
        }
    }

    /**
     * Converts the value into a string to display
     *
     * @param value    The value to display
     * @param settings The settings of this stat
     * @return The output string
     */
    public Component displayValue(long value, @NotNull StatSettings settings) {
        return Component.text(String.valueOf(value));
    }

    /**
     * Converts the value into a string for displaying on a sign
     *
     * @param value    The value to display
     * @param settings The settings of this stat
     * @return The output string
     */
    public abstract Component displayValueSign(long value, @NotNull StatSettings settings);
}
