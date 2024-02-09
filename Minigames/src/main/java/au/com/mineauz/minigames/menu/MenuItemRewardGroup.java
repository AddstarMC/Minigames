package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuItemRewardGroup extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "RewardGroup_description";
    private final @NotNull RewardGroup group;
    private final @NotNull Rewards rewards;

    public MenuItemRewardGroup(@Nullable Material displayMat, @Nullable Component name, @NotNull RewardGroup group,
                               @NotNull Rewards rewards) {
        super(displayMat, name);
        this.group = group;
        this.rewards = rewards;
        updateDescription();
    }

    public MenuItemRewardGroup(@Nullable Material displayMat, @Nullable Component name,
                               @Nullable List<@NotNull Component> description, @NotNull RewardGroup group,
                               @NotNull Rewards rewards) {
        super(displayMat, name, description);
        this.group = group;
        this.rewards = rewards;
        updateDescription();
    }

    public void updateDescription() {
        List<RewardRarity> options = Arrays.asList(RewardRarity.values());

        int pos = options.indexOf(group.getRarity());
        int before = pos - 1;
        int after = pos + 1;
        if (before == -1) {
            before = options.size() - 1;
        }
        if (after == options.size()) {
            after = 0;
        }

        List<Component> description = new ArrayList<>();
        description.add(Component.text(options.get(before).toString(), NamedTextColor.GRAY));
        description.add(Component.text(group.getRarity().toString(), NamedTextColor.GREEN));
        description.add(Component.text(options.get(after).toString(), NamedTextColor.GRAY));

        setDescriptionPartAtEnd(DESCRIPTION_TOKEN, description);
    }


    @Override
    public ItemStack onClick() {
        group.setRarity(group.getRarity().getNextRarity());
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        group.setRarity(group.getRarity().getPreviousRarity());
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.equalsIgnoreCase("yes")) {
            rewards.removeGroup(group);
            getContainer().removeItem(this.getSlot());

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            return;
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("The selected group will not be removed from the rewards.", MinigameMessageType.ERROR);
    }

    @Override
    public ItemStack onShiftRightClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        String itemName = group.getName();

        mgPlayer.sendInfoMessage("Delete the reward group \"" + itemName + "\"? Type \"Yes\" to confirm.");
        mgPlayer.sendInfoMessage("The menu will automatically reopen in 10s if nothing is entered.");
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(10);
        return null;
    }

    @Override
    public ItemStack onDoubleClick() {
        Menu rewardMenu = new Menu(5, getName(), getContainer().getViewer());
        rewardMenu.setPreviousPage(getContainer());

        List<Component> des = new ArrayList<>();
        des.add("Click this with an item");
        des.add("to add it to rewards.");
        des.add("Click without an item");
        des.add("to add a money reward.");

        rewardMenu.addItem(new MenuItemRewardAdd("Add Item", des, MenuUtility.getCreateMaterial(), group), 43);
        rewardMenu.addItem(new MenuItemPage("Save " + getName(), MenuUtility.getSaveMaterial(), rewardMenu.getPreviousPage()), 44);

        List<MenuItem> mi = new ArrayList<>();
        for (RewardType item : group.getItems()) {
            mi.add(item.getMenuItem());
        }

        rewardMenu.addItems(mi);
        rewardMenu.displayMenu(getContainer().getViewer());
        return null;
    }
}
