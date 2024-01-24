package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemReward extends RewardType {

    private ItemStack item = new ItemStack(Material.DIAMOND);

    public ItemReward(Rewards rewards) {
        super(rewards);
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
    public void giveReward(MinigamePlayer player) {
        if (player.isInMinigame())
            player.addRewardItem(item);
        else
            player.getPlayer().getInventory().addItem(item);
        player.sendMessage(MinigameMessageManager.getMinigamesMessage("reward.item", item.getAmount(),
                WordUtils.capitalize(item.getType().toString())), MinigameMessageType.WIN);
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
        private final ItemReward reward;
        private List<String> options = new ArrayList<>();

        public MenuItemReward(ItemReward reward) {
            super("PLACEHOLDER", List.of("Click with item", "to change."), Material.DIAMOND);
            setItem(reward.getRewardItem());
            for (RewardRarity rarity : RewardRarity.values()) {
                options.add(rarity.toString());
            }
            this.reward = reward;
            updateDescription();
        }

        @Override
        public void setItem(ItemStack item) {
            super.setItem(item);
            ItemMeta meta = getItem().getItemMeta();
            meta.setDisplayName(ChatColor.RESET + WordUtils.capitalize(item.getType().toString().replace("_", " ")));
            getItem().setItemMeta(meta);
        }

        @Override
        public ItemStack onClickWithItem(ItemStack item) {
            setItem(item);
            setRewardItem(item.clone());
            updateDescription();
            return getItem();
        }

        public void updateDescription() {
            List<String> description;
            if (options == null) {
                options = new ArrayList<>();
                for (RewardRarity rarity : RewardRarity.values()) {
                    options.add(rarity.toString());
                }
            }
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
            if (ind == options.size())
                ind = 0;

            setRarity(RewardRarity.valueOf(options.get(ind)));
            updateDescription();

            return getItem();
        }

        @Override
        public ItemStack onRightClick() {
            int ind = options.lastIndexOf(getRarity().toString());
            ind--;
            if (ind == -1)
                ind = options.size() - 1;

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
