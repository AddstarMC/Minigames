package au.com.mineauz.minigames.minigame.reward;

public enum RewardRarity {
    VERY_COMMON(0.5),
    COMMON(0.25),
    NORMAL(0.1),
    RARE(0.02),
    VERY_RARE(0);

    private final double rarity;

    RewardRarity(double r) {
        rarity = r;
    }

    public double getRarity() {
        return rarity;
    }

    public RewardRarity getPreviousRarity() {
        return switch (this) {
            case VERY_COMMON -> COMMON;
            case COMMON -> NORMAL;
            case NORMAL -> RARE;
            default -> VERY_RARE;
        };

    }

    public RewardRarity getNextRarity() {
        return switch (this) {
            case VERY_RARE -> RARE;
            case RARE -> NORMAL;
            case NORMAL -> COMMON;
            default -> VERY_COMMON;
        };

    }
}
