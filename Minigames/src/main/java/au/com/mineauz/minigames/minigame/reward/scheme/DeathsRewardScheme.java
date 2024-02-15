package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.MinigameStatistics;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;

public class DeathsRewardScheme extends HierarchyRewardScheme<Integer> {
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
        return Integer.valueOf(key);
    }

    @Override
    protected String getMenuItemDescName(Integer value) {
        return "Deaths: " + value;
    }

    @Override
    protected Integer getValue(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
        return (int) data.getStat(MinigameStatistics.Deaths);
    }

    @Override
    protected Component getMenuItemName(Integer value) {
        return Component.text(value.toString());
    }
}
