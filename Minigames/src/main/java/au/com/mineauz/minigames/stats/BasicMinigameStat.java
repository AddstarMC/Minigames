package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.MinigameUtils;
import net.kyori.adventure.text.Component;

import java.time.Duration;

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
    public Component displayValueSign(long value, StatSettings settings) {
        if (this == MinigameStats.CompletionTime) {
            return MinigameUtils.convertTime(Duration.ofMillis(value));
        } else {
            return value + " " + settings.getDisplayName();
        }
    }
}
