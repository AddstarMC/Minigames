package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class MenuItemRewardGroupAdd extends MenuItem {
    private final Rewards rewards;

    public MenuItemRewardGroupAdd(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Rewards rewards) {
        super(displayMat, langKey);
        this.rewards = rewards;
    }

    public MenuItemRewardGroupAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull Rewards rewards) {
        super(displayMat, name);
        this.rewards = rewards;
    }

    public MenuItemRewardGroupAdd(@Nullable Material displayMat, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull Rewards rewards) {
        super(displayMat, name, description);
        this.rewards = rewards;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        final int reopenSeconds = 30;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_REWARD_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))));
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(reopenSeconds);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        getContainer().cancelReopenTimer();

        entry = entry.replace(" ", "_");
        for (RewardGroup group : rewards.getGroups()) {
            if (group.getName().equals(entry)) {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                        MgMenuLangKey.MENU_REWARD_ERROR_GROUPEXISTS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }

        RewardGroup group = rewards.addGroup(entry, RewardRarity.NORMAL);

        MenuItemRewardGroup mrg = new MenuItemRewardGroup(Material.CHEST,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_GROUP_NAME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry)), group, rewards);
        getContainer().addItem(mrg);

        getContainer().displayMenu(getContainer().getViewer());
    }
}
