package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;

/**
 * These are the components of {@link StatFormat} indicating what
 * fields are available
 */
public enum StatValueField {
    Last("", "Last Value"),
    Min("_min", "Minimum"),
    Max("_max", "Maximum"),
    Total("_total", "Total");

    private final Component title;
    private final String suffix;

    StatValueField(String suffix, LangKey titleLangKey) {
        this.suffix = suffix;
        this.title = MinigameMessageManager.getMgMessage(titleLangKey);
    }

    public Component getTitle() {
        return title;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Can be used to apply this fields function
     *
     * @param currentValue The current value of this stat before update
     * @param newValue     The value being applied to this stat
     * @return The resulting value that should be used for this field
     */
    public long apply(long currentValue, long newValue) {
        return switch (this) {
            case Last -> newValue;
            case Min -> Math.min(currentValue, newValue);
            case Max -> Math.max(currentValue, newValue);
            case Total -> currentValue + newValue;
        };
    }
}
