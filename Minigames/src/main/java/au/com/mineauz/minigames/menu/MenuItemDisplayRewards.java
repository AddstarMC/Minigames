package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.reward.Rewards;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemDisplayRewards extends MenuItem {

    private final Rewards rewards;

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
