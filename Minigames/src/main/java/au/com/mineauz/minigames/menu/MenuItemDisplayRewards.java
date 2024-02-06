package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.reward.Rewards;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemDisplayRewards extends MenuItem {

    private final Rewards rewards;

    public MenuItemDisplayRewards(Component name, Material displayItem, Rewards rewards) {
        super(name, displayItem);
        this.rewards = rewards;
    }

    public MenuItemDisplayRewards(Component name, List<Component> description, Material displayItem, Rewards rewards) {
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
