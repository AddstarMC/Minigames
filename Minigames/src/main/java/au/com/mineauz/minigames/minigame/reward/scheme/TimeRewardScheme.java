package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.MinigameStatistics;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    /**
     * in seconds
     */
    @Override
    protected Component getMenuItemDescName(@NotNull Integer value) {
        return MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_TIME_DESCRIPTION,
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(value), true)));
    }

    /**
     * in seconds
     */
    @Override
    protected Integer getValue(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(data.getStat(MinigameStatistics.CompletionTime));
    }

    /**
     * in seconds
     */
    @Override
    protected Component getMenuItemName(@NotNull Integer value) {
        return MinigameUtils.convertTime(Duration.ofSeconds(value), true);
    }
}
