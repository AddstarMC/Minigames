package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemWhitelistBlock extends MenuItem {

    private List<Material> whitelist;

    public MenuItemWhitelistBlock(Material displayItem, List<Material> whitelist) {
        super(MinigameUtils.capitalize(displayItem.toString().replace("_", " ")), displayItem);
        setDescription(MinigameUtils.stringToList("Right Click to remove"));
        this.whitelist = whitelist;
    }

    @Override
    public ItemStack onRightClick() {
        whitelist.remove(getItem().getType());
        getContainer().removeItem(getSlot());
        return null;
    }
}
