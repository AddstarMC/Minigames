package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class HierarchyRewardScheme<T extends Comparable<T>> implements RewardScheme {
    private final EnumFlag<Comparison> comparisonType;
    private final BooleanFlag enableRewardsOnLoss;
    private final BooleanFlag lossUsesSecondary;

    private final TreeMap<T, Rewards> primaryRewards;
    private final TreeMap<T, Rewards> secondaryRewards;

    public HierarchyRewardScheme() {
        primaryRewards = new TreeMap<>();
        secondaryRewards = new TreeMap<>();

        comparisonType = new EnumFlag<>(Comparison.Greater, "comparison");
        enableRewardsOnLoss = new BooleanFlag(false, "loss-rewards");
        lossUsesSecondary = new BooleanFlag(true, "loss-use-secondary");
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        return Map.of(
                "comparison", comparisonType,
                "loss-rewards", enableRewardsOnLoss,
                "loss-use-secondary", lossUsesSecondary);
    }

    @Override
    public void addMenuItems(final Menu menu) {
        menu.addItem(new MenuItemEnum<Comparison>(Material.COMPARATOR, "Comparison Type", getConfigurationTypeCallback(), Comparison.class));
        menu.addItem(enableRewardsOnLoss.getMenuItem(Material.LEVER, "Award On Loss", List.of("When on, awards will still", "be given to losing", "players")));
        menu.addItem(lossUsesSecondary.getMenuItem(Material.LEVER, "Losers Get Secondary", List.of("When on, the losers", "will only get the", "secondary reward")));
        menu.addItem(new MenuItemNewLine());

        MenuItemCustom primary = new MenuItemCustom(Material.CHEST, "Primary Rewards");
        primary.setClick(() -> {
            showRewardsMenu(primaryRewards, menu.getViewer(), menu);
            return null;
        });

        MenuItemCustom secondary = new MenuItemCustom(Material.CHEST, "Secondary Rewards");
        secondary.setClick(() -> {
            showRewardsMenu(secondaryRewards, menu.getViewer(), menu);
            return null;
        });

        menu.addItem(primary);
        menu.addItem(secondary);
    }

    private void showRewardsMenu(TreeMap<T, Rewards> rewards, MinigamePlayer player, Menu parent) {
        Menu submenu = new Menu(6, MgMenuLangKey.MENU_REWARD_NAME, player);

        for (T key : rewards.keySet()) {
            submenu.addItem(new MenuItemRewardPair(Material.CHEST, rewards, key));
        }

        submenu.addItem(new MenuItemAddReward(MenuUtility.getCreateMaterial(), "Add Reward Set", rewards), submenu.getSize() - 2);
        submenu.addItem(new MenuItemBack(parent), submenu.getSize() - 1);

        submenu.setPreviousPage(parent);

        submenu.displayMenu(player);
    }

    protected abstract T getValue(MinigamePlayer player, StoredGameStats data, Minigame minigame);

    @Override
    public void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion) {
        T value = getValue(player, data, minigame);
        Rewards reward;

        TreeMap<T, Rewards> rewards = (firstCompletion ? primaryRewards : secondaryRewards);

        // Calculate rewards
        switch (comparisonType.getFlag()) {
            case Equal -> reward = rewards.get(value);
            case Lesser -> {
                reward = null;
                for (Entry<T, Rewards> entry : rewards.entrySet()) {
                    if (value.compareTo(entry.getKey()) < 0) {
                        reward = entry.getValue();
                        break;
                    }
                }
            }
            case Greater -> {
                reward = null;
                for (Entry<T, Rewards> entry : rewards.descendingMap().entrySet()) {
                    if (value.compareTo(entry.getKey()) > 0) {
                        reward = entry.getValue();
                        break;
                    }
                }
            }
            default -> throw new AssertionError();
        }

        // Apply reward
        if (reward != null) {
            List<RewardType> rewardItems = reward.getReward();
            for (RewardType item : rewardItems) {
                item.giveReward(player);
            }
        }
    }

    @Override
    public void awardPlayerOnLoss(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
        if (enableRewardsOnLoss.getFlag())
            awardPlayer(player, data, minigame, lossUsesSecondary.getFlag());
    }

    @Override
    public void save(ConfigurationSection config) {
        ConfigurationSection primary = config.createSection("score-primary");
        ConfigurationSection secondary = config.createSection("score-secondary");

        save(primaryRewards, primary);
        save(secondaryRewards, secondary);
    }

    private void save(TreeMap<T, Rewards> map, ConfigurationSection section) {
        for (Entry<T, Rewards> entry : map.entrySet()) {
            ConfigurationSection scoreSection = section.createSection(String.valueOf(entry.getKey()));
            entry.getValue().save(scoreSection);
        }
    }

    @Override
    public void load(ConfigurationSection config) {
        ConfigurationSection primary = config.getConfigurationSection("score-primary");
        ConfigurationSection secondary = config.getConfigurationSection("score-secondary");
        if (primary != null)
            load(primaryRewards, primary);
        if (secondary != null)
            load(secondaryRewards, secondary);
    }

    protected abstract T loadValue(String key);

    private void load(TreeMap<T, Rewards> map, @NotNull ConfigurationSection section) {
        map.clear();
        for (String key : section.getKeys(false)) {
            T value = loadValue(key);

            ConfigurationSection subSection = section.getConfigurationSection(key);
            Rewards reward = new Rewards();
            if (subSection != null)
                reward.load(subSection);
            map.put(value, reward);
        }
    }

    private Callback<Comparison> getConfigurationTypeCallback() {
        return new Callback<>() {
            @Override
            public Comparison getValue() {
                return comparisonType.getFlag();
            }

            @Override
            public void setValue(Comparison value) {
                comparisonType.setFlag(value);
            }
        };
    }

    protected abstract Component getMenuItemName(T value);

    protected abstract Component getMenuItemDescName(T value);

    protected abstract T increment(T value);

    protected abstract T decrement(T value);

    public enum Comparison {
        Greater,
        Equal,
        Lesser
    }

    private class MenuItemRewardPair extends MenuItem {
        private final static String DESCRIPTION_TOKEN = "RewardPair_description";
        private final @NotNull Rewards reward;
        private final @NotNull TreeMap<@NotNull T, @NotNull Rewards> map;
        private @NotNull T value;

        public MenuItemRewardPair(@Nullable Material displayMat, @NotNull TreeMap<@NotNull T, @NotNull Rewards> map,
                                  @NotNull T value) {
            super(displayMat, getMenuItemName(value));

            this.map = map;
            this.value = value;
            this.reward = map.get(value);

            updateDescription();
        }

        private void updateDescription() {
            List<Component> description = List.of(
                    getMenuItemDescName(value).color(NamedTextColor.GREEN),
                    MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARDPAIR_EDIT),
                    MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK)
            );

            setDescriptionPartAtEnd(DESCRIPTION_TOKEN, description);

            // Update name
            ItemStack item = getDisplayItem();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(getMenuItemName(value));
                item.setItemMeta(meta);
            }

            setDisplayItem(item);
        }

        private void updateValue(T newValue) {
            map.remove(value);
            value = newValue;
            map.put(value, reward);
        }

        @Override
        // Increase score
        public ItemStack onClick() {
            T nextValue = increment(value);
            while (map.containsKey(nextValue)) {
                nextValue = increment(nextValue);
            }

            updateValue(nextValue);

            updateDescription();
            return getDisplayItem();
        }

        @Override
        // Decrease score
        public ItemStack onRightClick() {
            T nextValue = decrement(value);
            while (map.containsKey(nextValue)) {
                nextValue = decrement(nextValue);
            }

            updateValue(nextValue);

            updateDescription();
            return getDisplayItem();
        }

        @Override
        // Open editor
        public ItemStack onDoubleClick() {
            MinigamePlayer mgPlayer = getContainer().getViewer();
            mgPlayer.setNoClose(true);
            mgPlayer.getPlayer().closeInventory();
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_HIERARCHY_ENTERCHAT,
                    Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(10))));

            mgPlayer.setManualEntry(this);
            getContainer().startReopenTimer(10);

            return null;
        }

        @Override
        public void checkValidEntry(String entry) {
            try {
                T value = loadValue(entry);
                if (map.containsKey(value)) {
                    MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.REWARDSCHEME_ERROR_DUPLICATE);
                } else {
                    updateValue(value);
                    updateDescription();
                }
            } catch (IllegalArgumentException e) {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.REWARDSCHEME_ERROR_INVALID);
            }

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
        }

        @Override
        // Open rewards
        public ItemStack onShiftClick() {
            Menu rewardMenu = reward.createMenu(getName(), getContainer().getViewer(), getContainer());

            rewardMenu.displayMenu(getContainer().getViewer());
            return null;
        }

        @Override
        // Remove
        public ItemStack onShiftRightClick() {
            getContainer().removeItem(getSlot());
            map.remove(value);

            return getDisplayItem();
        }
    }

    private class MenuItemAddReward extends MenuItem {
        private final @NotNull TreeMap<@NotNull T, @NotNull Rewards> map;

        public MenuItemAddReward(@Nullable Material displayMat, @NotNull Component name,
                                 @NotNull TreeMap<@NotNull T, @NotNull Rewards> map) {
            super(displayMat, name);

            this.map = map;
        }

        @Override
        public ItemStack onClick() {
            MinigamePlayer mgPlayer = getContainer().getViewer();
            mgPlayer.setNoClose(true);
            mgPlayer.getPlayer().closeInventory();
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_HIERARCHY_ENTERCHAT,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(10)));

            mgPlayer.setManualEntry(this);
            getContainer().startReopenTimer(10);

            return null;
        }

        @Override
        public void checkValidEntry(String entry) {
            boolean show = true;

            try {
                T value = loadValue(entry);
                Rewards reward = new Rewards();

                if (map.containsKey(value)) {
                    MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.REWARDSCHEME_ERROR_DUPLICATE);
                } else {
                    map.put(value, reward);
                    showRewardsMenu(map, getContainer().getViewer(), getContainer().getPreviousPage());
                    show = false;
                }
            } catch (IllegalArgumentException e) {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MinigameLangKey.REWARDSCHEME_ERROR_INVALID);
            }

            getContainer().cancelReopenTimer();
            if (show) {
                getContainer().displayMenu(getContainer().getViewer());
            }
        }
    }
}
