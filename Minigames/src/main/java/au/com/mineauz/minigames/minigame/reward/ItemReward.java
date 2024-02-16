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
        private final static String DESCRIPTION_REWARD_TOKEN = "Reward_description";
        private final @NotNull ItemReward reward;
        private final @NotNull List<RewardRarity> rarities;

        public MenuItemReward(@NotNull ItemReward reward) {
            super(reward.item.clone(), reward.item.getItemMeta().displayName());

            rarities = List.of(RewardRarity.values());
            this.reward = reward;

            updateDescriptionRarity();
        }

        @Override
        public ItemStack onClickWithItem(@NotNull ItemStack item) {
            setRewardItem(item.clone());
            setDisplayItem(item);

            // update lore
            updateDescriptionRarity();

            // update display name
            ItemMeta meta = super.getDisplayItem().getItemMeta();
            meta.displayName(item.getItemMeta().displayName());
            super.getDisplayItem().setItemMeta(meta);

            return super.getDisplayItem();
        }

        public void updateDescriptionRarity() {
            List<Component> description;
            int pos = rarities.indexOf(getRarity());
            int before = pos - 1;
            int after = pos + 1;
            if (before == -1) {
                before = rarities.size() - 1;
            }
            if (after == rarities.size()) {
                after = 0;
            }

            description = new ArrayList<>();
            description.add(rarities.get(before).getDisplayName().color(NamedTextColor.GRAY));
            description.add(getRarity().getDisplayName().color(NamedTextColor.GREEN));
            description.add(rarities.get(after).getDisplayName().color(NamedTextColor.GRAY));
            description.add(MinigameMessageManager.getMgMessage(
                    MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK).color(NamedTextColor.DARK_PURPLE));

            setDescriptionPartAtIndex(DESCRIPTION_REWARD_TOKEN, 0, description);
        }

        @Override
        public ItemStack onClick() {
            int ind = rarities.lastIndexOf(getRarity());
            ind++;
            if (ind == rarities.size()) {
                ind = 0;
            }

            setRarity(rarities.get(ind));
            updateDescriptionRarity();

            return getDisplayItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = rarities.lastIndexOf(getRarity());
            ind--;
            if (ind == -1) {
                ind = rarities.size() - 1;
            }

            setRarity(rarities.get(ind));
            updateDescriptionRarity();

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
