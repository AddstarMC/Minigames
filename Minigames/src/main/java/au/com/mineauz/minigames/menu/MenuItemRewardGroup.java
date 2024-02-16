package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRewardGroup extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "RewardGroup_description";
    private final static @NotNull List<@NotNull RewardRarity> options = List.of(RewardRarity.values());
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
        int pos = options.indexOf(group.getRarity());
        int before = pos - 1;
        int after = pos + 1;
        if (before == -1) {
            before = options.size() - 1;
        }
        if (after == options.size()) {
            after = 0;
        }

        List<Component> description = new ArrayList<>(3);
        description.add(Component.text(options.get(before).toString(), NamedTextColor.GRAY));
        description.add(Component.text(group.getRarity().toString(), NamedTextColor.GREEN));
        description.add(Component.text(options.get(after).toString(), NamedTextColor.GRAY));

        setDescriptionPart(DESCRIPTION_TOKEN, description);
    }


    @Override
    public ItemStack onClick() {
        int ind = options.lastIndexOf(group.getRarity());
        ind++;
        if (ind == options.size()) {
            ind = 0;
        }

        group.setRarity(options.get(ind));

        updateDescription();

        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        int ind = options.lastIndexOf(group.getRarity());
        ind--;
        if (ind == -1) {
            ind = options.size() - 1;
        }

        group.setRarity(options.get(ind));
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public void checkValidEntry(String entry) {
        getContainer().cancelReopenTimer();

        if (entry.equalsIgnoreCase("yes")) { // todo?
            rewards.removeGroup(group);
            getContainer().removeItem(this.getSlot());

            getContainer().displayMenu(getContainer().getViewer());
        } else {
            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MgMenuLangKey.MENU_REWARD_NOTREMOVED);

            getContainer().displayMenu(getContainer().getViewer());
        }
    }

    @Override
    public ItemStack onShiftRightClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();

        final int reopenSeconds = 10;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_REWARD_GROUP_ENTERCHAT,
                Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), group.getName()),
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))));
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(reopenSeconds);
        return null;
    }

    @Override
    public ItemStack onShiftClick() {
        Menu rewardMenu = new Menu(5, getName(), getContainer().getViewer());
        rewardMenu.setPreviousPage(getContainer());

        rewardMenu.addItem(new MenuItemRewardAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_REWARD_ITEM_ADD_NAME,
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_REWARD_ITEM_ADD_DESCRIPTION), group), 43);
        rewardMenu.addItem(new MenuItemPage(MenuUtility.getSaveMaterial(),
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SAVE_NAME,
                        Placeholder.component(MinigamePlaceHolderKey.REWARD.getKey(), getName())), rewardMenu.getPreviousPage()), 44);

        List<MenuItem> menuItems = new ArrayList<>(group.getItems().size());
        for (RewardType item : group.getItems()) {
            menuItems.add(item.getMenuItem());
        }

        rewardMenu.addItems(menuItems);
        rewardMenu.displayMenu(getContainer().getViewer());
        return null;
    }
}
