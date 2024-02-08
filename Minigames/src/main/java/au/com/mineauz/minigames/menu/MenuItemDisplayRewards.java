package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.reward.Rewards;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemDisplayRewards extends MenuItem {
    private final @NotNull Rewards rewards;

    public MenuItemDisplayRewards(@Nullable Material displayMat, @Nullable Component name, @NotNull Rewards rewards) {
        super(displayMat, name);
        this.rewards = rewards;
    }

    public MenuItemDisplayRewards(@Nullable Material displayMat, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull Rewards rewards) {
        super(displayMat, name, description);
        this.rewards = rewards;
    }

    @Override
    public ItemStack onClick() {
        Menu rewardMenu = rewards.createMenu(getName(), getContainer().getViewer(), getContainer());

        rewardMenu.displayMenu(getContainer().getViewer());
        return null;
    }
}
