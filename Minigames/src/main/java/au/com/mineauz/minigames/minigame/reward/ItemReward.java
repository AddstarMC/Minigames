package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemReward extends RewardType {
    private ItemStack item = new ItemStack(Material.DIAMOND);

    public ItemReward(Rewards rewards) {
        super(rewards);
    }

    public static ItemReward getMinigameReward(@NotNull Rewards rewards) {
        return (ItemReward) RewardTypes.getRewardType(RewardTypes.MgRewardType.ITEM.getName(), rewards);
    }

    @Override
    public String getName() {
        return "ITEM";
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void giveReward(@NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.isInMinigame()) {
            mgPlayer.addRewardItem(item);
        } else {
            Collection<ItemStack> notAddedStacks = mgPlayer.getPlayer().getInventory().addItem(item).values();
            for (ItemStack notAdded : notAddedStacks) { // drop items that didn't fit into inventory
                mgPlayer.getLocation().getWorld().dropItemNaturally(mgPlayer.getLocation(), notAdded);
            }

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.WIN, MinigameLangKey.REWARD_ITEM,
                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(item.getAmount())),
                    Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), item.displayName()));
        }
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItemReward(this);
    }

    @Override
    public void saveReward(String path, ConfigurationSection config) {
        config.set(path, item);
    }

    @Override
    public void loadReward(String path, ConfigurationSection config) {
        item = config.getItemStack(path);
    }

    public ItemStack getRewardItem() {
        return item;
    }

    public void setRewardItem(ItemStack item) {
        this.item = item;
    }

    private class MenuItemReward extends MenuItem {
        private final @NotNull ItemReward reward;
        private final @NotNull List<String> options = new ArrayList<>();

        public MenuItemReward(@NotNull ItemReward reward) {
            super(Material.DIAMOND, Component.translatable(Material.DIAMOND.translationKey()));
            setItem(reward.getRewardItem());
            for (RewardRarity rarity : RewardRarity.values()) {
                options.add(rarity.toString());
            }
            this.reward = reward;
            updateDescription();
        }

        @Override
        public void setItem(@NotNull ItemStack item) {
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.translatable(item.translationKey()));
            item.setItemMeta(meta);
        }

        @Override
        public ItemStack onClickWithItem(ItemStack item) {
            setItem(item);
            setRewardItem(item.clone());
            updateDescription();
            return getItem();
        }

        public void updateDescription() {
            List<Component> description;
            int pos = options.indexOf(getRarity().toString());
            int before = pos - 1;
            int after = pos + 1;
            if (before == -1)
                before = options.size() - 1;
            if (after == options.size())
                after = 0;

            if (getDescription() != null) {
                description = getDescription();
                if (getDescription().size() >= 3) {
                    String desc = ChatColor.stripColor(getDescription().get(1));

                    if (options.contains(desc)) {
                        description.set(0, ChatColor.GRAY + options.get(before));
                        description.set(1, ChatColor.GREEN + getRarity().toString());
                        description.set(2, ChatColor.GRAY + options.get(after));
                    } else {
                        description.add(0, ChatColor.GRAY + options.get(before));
                        description.add(1, ChatColor.GREEN + getRarity().toString());
                        description.add(2, ChatColor.GRAY + options.get(after));
                        description.add(3, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
                    }
                } else {
                    description.add(0, ChatColor.GRAY + options.get(before));
                    description.add(1, ChatColor.GREEN + getRarity().toString());
                    description.add(2, ChatColor.GRAY + options.get(after));
                    description.add(3, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
                }
            } else {
                description = new ArrayList<>();
                description.add(ChatColor.GRAY + options.get(before));
                description.add(ChatColor.GREEN + getRarity().toString());
                description.add(ChatColor.GRAY + options.get(after));
                description.add(3, ChatColor.DARK_PURPLE + "Shift + Right Click to remove");
            }

            setDescription(description);
        }

        @Override
        public ItemStack onClick() {
            int ind = options.lastIndexOf(getRarity().toString());
            ind++;
            if (ind == options.size()) {
                ind = 0;
            }

            setRarity(RewardRarity.valueOf(options.get(ind)));
            updateDescription();

            return getItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = options.lastIndexOf(getRarity().toString());
            ind--;
            if (ind == -1) {
                ind = options.size() - 1;
            }

            setRarity(RewardRarity.valueOf(options.get(ind)));
            updateDescription();

            return getItem();
        }

        @Override
        public ItemStack onShiftRightClick() {
            getRewards().removeReward(reward);
            getContainer().removeItem(getSlot());
            return null;
        }
    }
}
