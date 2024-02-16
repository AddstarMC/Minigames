package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RewardRarity {
    VERY_COMMON(0.5, MinigameLangKey.REWARDRARITY_VERYCOMMON),
    COMMON(0.25, MinigameLangKey.REWARDRARITY_COMMON),
    NORMAL(0.1, MinigameLangKey.REWARDRARITY_NORMAL),
    RARE(0.02, MinigameLangKey.REWARDRARITY_RARE),
    VERY_RARE(0, MinigameLangKey.REWARDRARITY_VERYRARE);

    private final double rarity;
    private final Component displayName;

    RewardRarity(double r, LangKey langKey) {
        rarity = r;
        displayName = MinigameMessageManager.getMgMessage(langKey);
    }

    @Contract("null -> null")
    public static @Nullable RewardRarity matchRarity(@Nullable String toMatch) {
        for (RewardRarity rarity : RewardRarity.values()) {
            if (rarity.toString().equalsIgnoreCase(toMatch)) {
                return rarity;
            }
        }

        return null;
    }

    public double getRarity() {
        return rarity;
    }

    /**
     * get the next highest rarity or itself if it already is the highest one
     */
    public @NotNull RewardRarity getHigherRarity() {
        return switch (this) {
            case VERY_COMMON -> COMMON;
            case COMMON -> NORMAL;
            case NORMAL -> RARE;
            case RARE, VERY_RARE -> VERY_RARE;
        };
    }

    /**
     * get the next lowest rarity or itself if it already is the lowest one
     */
    public @NotNull RewardRarity getLowerRarity() {
        return switch (this) {
            case VERY_RARE -> RARE;
            case RARE -> NORMAL;
            case NORMAL -> COMMON;
            case COMMON, VERY_COMMON -> VERY_COMMON;
        };
    }

    public Component getDisplayName() {
        return displayName;
    }
}
