package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemDisplayRewards extends MenuItem {

    private Rewards rewards;

    public MenuItemDisplayRewards(String name, Material displayItem, Rewards rewards) {
        super(name, displayItem);
        this.rewards = rewards;
    }

    public MenuItemDisplayRewards(String name, List<String> description, Material displayItem, Rewards rewards) {
        super(name, description, displayItem);
        this.rewards = rewards;
    }

    @Override
    public ItemStack onClick() {
        Menu rewardMenu = rewards.createMenu(getName(), getContainer().getViewer(), getContainer());

        rewardMenu.displayMenu(getContainer().getViewer());
        return null;
    }

}
