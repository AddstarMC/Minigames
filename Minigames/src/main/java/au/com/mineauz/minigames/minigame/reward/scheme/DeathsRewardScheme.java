package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.MinigameStatistics;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

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
    protected Component getMenuItemDescName(Integer value) {
        return MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_DEATHS_DESCRIPTION,
                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(value)));
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
