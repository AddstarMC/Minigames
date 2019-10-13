package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StoredGameStats;

public class TimeRewardScheme extends HierarchyRewardScheme<Integer> {
    @Override
    protected Integer decrement(Integer value) {
        return value - 1;
    }

    @Override
    protected Integer increment(Integer value) {
        return value + 1;
    }

    @Override
    protected Integer loadValue(String key) {
        int value = Integer.parseInt(key);
        if (value <= 0) {
            throw new IllegalArgumentException();
        }

        return value;
    }

    @Override
    protected String getMenuItemDescName(Integer value) {
        return "Time: " + MinigameUtils.convertTime(value, true);
    }

    @Override
    protected Integer getValue(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
        return (int) (data.getStat(MinigameStats.CompletionTime) / 1000);
    }

    @Override
    protected String getMenuItemName(Integer value) {
        return MinigameUtils.convertTime(value, true);
    }
}
