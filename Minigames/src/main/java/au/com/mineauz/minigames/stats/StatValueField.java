package au.com.mineauz.minigames.stats;

/**
 * These are the components of {@link StatFormat} indicating what
 * fields are available
 */
public enum StatValueField {
    Last("", "Last Value"),
    Min("_min", "Minimum"),
    Max("_max", "Maximum"),
    Total("_total", "Total");

    private final String title;
    private final String suffix;

    StatValueField(String suffix, String title) {
        this.suffix = suffix;
        this.title = title;
    }

    public String getTitle() {
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
        switch (this) {
            case Last:
                return newValue;
            case Min:
                return Math.min(currentValue, newValue);
            case Max:
                return Math.max(currentValue, newValue);
            case Total:
                return currentValue + newValue;
            default:
                // Should be implemented
                throw new AssertionError();
        }
    }
}
