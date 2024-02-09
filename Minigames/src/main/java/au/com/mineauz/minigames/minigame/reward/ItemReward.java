package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
        private final static String DESCRIPTION_TOKEN = "Reward_description";
        private final @NotNull ItemReward reward;
        private final @NotNull List<RewardRarity> options;

        public MenuItemReward(@NotNull ItemReward reward) {
            super(Material.DIAMOND, "PLACEHOLDER", List.of("Click with item", "to change."));
            setDisplayItem(reward.getRewardItem());
            options = Arrays.asList(RewardRarity.values());
            this.reward = reward;
            updateDescription();
        }

        @Override
        public void setDisplayItem(@NotNull ItemStack item) {
            super.setDisplayItem(item);
            ItemMeta meta = getDisplayItem().getItemMeta();
            meta.displayName(Component.translatable(item.translationKey()));
            getDisplayItem().setItemMeta(meta);
        }

        @Override
        public ItemStack onClickWithItem(ItemStack item) {
            setDisplayItem(item);
            setRewardItem(item.clone());
            updateDescription();
            return getDisplayItem();
        }

        public void updateDescription() {
            List<Component> description;
            int pos = options.indexOf(getRarity());
            int before = pos - 1;
            int after = pos + 1;
            if (before == -1) {
                before = options.size() - 1;
            }
            if (after == options.size()) {
                after = 0;
            }

            description = new ArrayList<>();
            description.add(options.get(before).getDisplayName().color(NamedTextColor.GRAY));
            description.add(getRarity().getDisplayName().color(NamedTextColor.GREEN));
            description.add(options.get(after).getDisplayName().color(NamedTextColor.GRAY));
            description.add(MinigameMessageManager.getMgMessage(
                    MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK).color(NamedTextColor.DARK_PURPLE));

            setDescriptionPartAtEnd(DESCRIPTION_TOKEN, description);
        }

        @Override
        public ItemStack onClick() {
            int ind = options.lastIndexOf(getRarity());
            ind++;
            if (ind == options.size()) {
                ind = 0;
            }

            setRarity(options.get(ind));
            updateDescription();

            return getDisplayItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = options.lastIndexOf(getRarity());
            ind--;
            if (ind == -1) {
                ind = options.size() - 1;
            }

            setRarity(options.get(ind));
            updateDescription();

            return getDisplayItem();
        }

        @Override
        public ItemStack onShiftRightClick() {
            getRewards().removeReward(reward);
            getContainer().removeItem(getSlot());
            return null;
        }
    }
}
