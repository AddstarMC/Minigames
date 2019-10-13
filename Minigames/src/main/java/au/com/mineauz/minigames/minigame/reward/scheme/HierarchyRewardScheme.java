package au.com.mineauz.minigames.minigame.reward.scheme;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.stats.StoredGameStats;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
        return ImmutableMap.<String, Flag<?>>builder()
                .put("comparison", comparisonType)
                .put("loss-rewards", enableRewardsOnLoss)
                .put("loss-use-secondary", lossUsesSecondary)
                .build();
    }

    @Override
    public void addMenuItems(final Menu menu) {
        menu.addItem(new MenuItemList("Comparison Type", Material.COMPARATOR, getConfigurationTypeCallback(), Arrays.stream(Comparison.values()).map(Functions.toStringFunction()).collect(Collectors.toList())));
        menu.addItem(enableRewardsOnLoss.getMenuItem("Award On Loss", Material.LEVER, MinigameUtils.stringToList("When on, awards will still;be given to losing;players")));
        menu.addItem(lossUsesSecondary.getMenuItem("Losers Get Secondary", Material.LEVER, MinigameUtils.stringToList("When on, the losers;will only get the;secondary reward")));
        menu.addItem(new MenuItemNewLine());

        MenuItemCustom primary = new MenuItemCustom("Primary Rewards", Material.CHEST);
        primary.setClick(object -> {
            showRewardsMenu(primaryRewards, menu.getViewer(), menu);
            return null;
        });

        MenuItemCustom secondary = new MenuItemCustom("Secondary Rewards", Material.CHEST);
        secondary.setClick(object -> {
            showRewardsMenu(secondaryRewards, menu.getViewer(), menu);
            return null;
        });

        menu.addItem(primary);
        menu.addItem(secondary);
    }

    private void showRewardsMenu(TreeMap<T, Rewards> rewards, MinigamePlayer player, Menu parent) {
        Menu submenu = new Menu(6, "Rewards", player);

        for (T key : rewards.keySet()) {
            submenu.addItem(new MenuItemRewardPair(rewards, key, Material.CHEST));
        }

        submenu.addItem(new MenuItemAddReward(rewards, "Add Reward Set", Material.ITEM_FRAME), submenu.getSize() - 2);
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
            case Equal:
                reward = rewards.get(value);
                break;
            case Lesser:
                reward = null;
                for (Entry<T, Rewards> entry : rewards.entrySet()) {
                    if (value.compareTo(entry.getKey()) < 0) {
                        reward = entry.getValue();
                        break;
                    }
                }
                break;
            case Greater:
                reward = null;
                for (Entry<T, Rewards> entry : rewards.descendingMap().entrySet()) {
                    if (value.compareTo(entry.getKey()) > 0) {
                        reward = entry.getValue();
                        break;
                    }
                }
                break;
            default:
                throw new AssertionError();
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
        if(primary != null)
          load(primaryRewards, primary);
        if(secondary != null)
          load(secondaryRewards, secondary);
    }

    protected abstract T loadValue(String key);

    private void load(TreeMap<T, Rewards> map, @NotNull ConfigurationSection section) {
        map.clear();
        for (String key : section.getKeys(false)) {
            T value = loadValue(key);

            ConfigurationSection subSection = section.getConfigurationSection(key);
            Rewards reward = new Rewards();
            if(subSection != null)
              reward.load(subSection);
            map.put(value, reward);
        }
    }

    private Callback<String> getConfigurationTypeCallback() {
        return new Callback<String>() {
            @Override
            public String getValue() {
                return comparisonType.getFlag().name();
            }

            @Override
            public void setValue(String value) {
                comparisonType.setFlag(Comparison.valueOf(value));
            }
        };
    }

    protected abstract String getMenuItemName(T value);

    protected abstract String getMenuItemDescName(T value);

    protected abstract T increment(T value);

    protected abstract T decrement(T value);

    public enum Comparison {
        Greater,
        Equal,
        Lesser
    }

    private class MenuItemRewardPair extends MenuItem {
        private final Rewards reward;
        private T value;
        private final TreeMap<T, Rewards> map;

        public MenuItemRewardPair(TreeMap<T, Rewards> map, T value, Material displayItem) {
            super(getMenuItemName(value), displayItem);

            this.map = map;
            this.value = value;
            this.reward = map.get(value);

            updateDescription();
        }

        private void updateDescription() {
            List<String> description = Arrays.asList(
                    ChatColor.GREEN + getMenuItemDescName(value),
                    "Shift + Left click to edit rewards",
                    "Shift + Right click to remove"
            );

            setDescription(description);

            // Update name
            ItemStack item = getItem();
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
              meta.setDisplayName(getMenuItemName(value));
              item.setItemMeta(meta);
            }

            setItem(item);
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
            return getItem();
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
            return getItem();
        }

        @Override
        // Open editor
        public ItemStack onDoubleClick() {
            MinigamePlayer ply = getContainer().getViewer();
            ply.setNoClose(true);
            ply.getPlayer().closeInventory();
            ply.sendMessage("Enter the required value into chat, the menu will automatically reopen in 10s if nothing is entered.", MinigameMessageType.INFO);

            ply.setManualEntry(this);
            getContainer().startReopenTimer(10);

            return null;
        }

        @Override
        public void checkValidEntry(String entry) {
            try {
                T value = loadValue(entry);
                if (map.containsKey(value)) {
                    getContainer().getViewer().sendMessage("You cannot add duplicate entries", MinigameMessageType.ERROR);
                } else {
                    updateValue(value);
                    updateDescription();
                }
            } catch (IllegalArgumentException e) {
                getContainer().getViewer().sendMessage("Invalid value entry!", MinigameMessageType.ERROR);
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

            return getItem();
        }
    }

    private class MenuItemAddReward extends MenuItem {
        private final TreeMap<T, Rewards> map;

        public MenuItemAddReward(TreeMap<T, Rewards> map, String name, Material displayItem) {
            super(name, displayItem);

            this.map = map;
        }

        @Override
        public ItemStack onClick() {
            MinigamePlayer ply = getContainer().getViewer();
            ply.setNoClose(true);
            ply.getPlayer().closeInventory();
            ply.sendMessage("Enter the required value into chat, the menu will automatically reopen in 10s if nothing is entered.", MinigameMessageType.INFO);

            ply.setManualEntry(this);
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
                    getContainer().getViewer().sendMessage("You cannot add duplicate entries", MinigameMessageType.ERROR);
                } else {
                    map.put(value, reward);
                    showRewardsMenu(map, getContainer().getViewer(), getContainer().getPreviousPage());
                    show = false;
                }
            } catch (IllegalArgumentException e) {
                getContainer().getViewer().sendMessage("Invalid value entry!", MinigameMessageType.ERROR);
            }

            getContainer().cancelReopenTimer();
            if (show) {
                getContainer().displayMenu(getContainer().getViewer());
            }
        }
    }
}
