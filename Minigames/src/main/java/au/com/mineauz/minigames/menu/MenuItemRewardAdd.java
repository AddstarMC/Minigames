package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.RewardTypes;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemRewardAdd extends MenuItem {

    private Rewards rewards;
    private RewardGroup group = null;

    public MenuItemRewardAdd(String name, Material displayItem, Rewards rewards) {
        super(name, displayItem);
        this.rewards = rewards;
    }

    public MenuItemRewardAdd(String name, List<String> description, Material displayItem, Rewards rewards) {
        super(name, description, displayItem);
        this.rewards = rewards;
    }

    public MenuItemRewardAdd(String name, Material displayItem, RewardGroup group) {
        super(name, displayItem);
        this.group = group;
    }

    public MenuItemRewardAdd(String name, List<String> description, Material displayItem, RewardGroup group) {
        super(name, description, displayItem);
        this.group = group;
    }

    @Override
    public ItemStack onClick() {
        Menu m = new Menu(6, "Select Reward Type", getContainer().getViewer());
        final Menu orig = getContainer();
        for (String type : RewardTypes.getAllRewardTypeNames()) {
            final MenuItemCustom custom = new MenuItemCustom("TYPE", Material.STONE);
            final RewardType rewType = RewardTypes.getRewardType(type, rewards);
            if (rewType.isUsable()) {
                ItemMeta meta = custom.getItem().getItemMeta();
                meta.setDisplayName(ChatColor.RESET + type);
                custom.getItem().setItemMeta(meta);
                custom.setItem(rewType.getMenuItem().getItem());
                custom.setClick(object -> {
                    if (rewards != null)
                        rewards.addReward(rewType);
                    else
                        group.addItem(rewType);
                    orig.displayMenu(orig.getViewer());
                    orig.addItem(rewType.getMenuItem());
                    return null;
                });
                m.addItem(custom);
            }
        }
        m.addItem(new MenuItemBack(orig), m.getSize() - 9);
        m.displayMenu(m.getViewer());
        return null;
    }
}
