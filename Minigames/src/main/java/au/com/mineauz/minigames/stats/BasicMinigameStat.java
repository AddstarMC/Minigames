package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BasicMinigameStat extends MinigameStat {
    BasicMinigameStat(@NotNull String name, @NotNull LangKey displayName, @NotNull StatFormat format) {
        super(name, MinigameMessageManager.getMgMessage(displayName), format);
    }

    BasicMinigameStat(@NotNull String name, @Nullable Component displayName, @NotNull StatFormat format) {
        super(name, displayName, format);
    }

    @Override
    public String displayValue(long value, StatSettings settings) {
        if (this == MinigameStatistics.CompletionTime) {
            return (value / 1000) + " seconds";
        } else {
            return value + " " + settings.getDisplayName();
        }
    }

    @Override
    public String displayValueSign(long value, StatSettings settings) {
        if (this == MinigameStatistics.CompletionTime) {
            return (value / 1000) + "sec";
        } else {
            return value + " " + settings.getDisplayName();
        }
    }
}
