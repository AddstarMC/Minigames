package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemRewardGroupAdd extends MenuItem {
    private final Rewards rewards;

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
        mgPlayer.sendInfoMessage("Enter reward group name into chat, the menu will automatically reopen in 30s if nothing is entered.");
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(30);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        entry = entry.replace(" ", "_");
        for (RewardGroup group : rewards.getGroups()) {
            if (group.getName().equals(entry)) {
                getContainer().getViewer().sendMessage("A reward group already exists by the name \"" + entry + "\"!", MinigameMessageType.ERROR);
                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }

        RewardGroup group = rewards.addGroup(entry, RewardRarity.NORMAL);

        MenuItemRewardGroup mrg = new MenuItemRewardGroup(Material.CHEST, entry + " Group", group, rewards);
        getContainer().addItem(mrg);

        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }

}
