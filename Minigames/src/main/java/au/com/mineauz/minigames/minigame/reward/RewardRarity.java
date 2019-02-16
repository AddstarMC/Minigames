package au.com.mineauz.minigames.minigame.reward;

public enum RewardRarity {
    VERY_COMMON(0.5),
    COMMON(0.25),
    NORMAL(0.1),
    RARE(0.02),
    VERY_RARE(0);

    private double rarity;

    RewardRarity(double r) {
        rarity = r;
    }

    public double getRarity() {
        return rarity;
    }

    public RewardRarity getPreviousRarity() {
        switch (this) {
            case VERY_COMMON:
                return COMMON;
            case COMMON:
                return NORMAL;
            case NORMAL:
                return RARE;
        }

        return VERY_RARE;
    }

    public RewardRarity getNextRarity() {
        switch (this) {
            case VERY_RARE:
                return RARE;
            case RARE:
                return NORMAL;
            case NORMAL:
                return COMMON;
        }

        return VERY_COMMON;
    }
}
