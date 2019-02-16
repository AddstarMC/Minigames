package au.com.mineauz.minigames.stats;

class BasicMinigameStat extends MinigameStat {
    BasicMinigameStat(String name, String displayName, StatFormat format) {
        super(name, format);
        setDisplayName(displayName);
    }

    @Override
    public String displayValue(long value, StatSettings settings) {
        if (this == MinigameStats.CompletionTime) {
            return (value / 1000) + " seconds";
        } else {
            return value + " " + settings.getDisplayName();
        }
    }

    @Override
    public String displayValueSign(long value, StatSettings settings) {
        if (this == MinigameStats.CompletionTime) {
            return (value / 1000) + "sec";
        } else {
            return value + " " + settings.getDisplayName();
        }
    }
}
