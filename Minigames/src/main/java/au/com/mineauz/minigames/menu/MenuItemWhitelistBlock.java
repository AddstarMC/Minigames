package au.com.mineauz.minigames.menu;

import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemWhitelistBlock extends MenuItem {

    private final List<Material> whitelist;

    public MenuItemWhitelistBlock(Material displayItem, List<Material> whitelist) {
        super(WordUtils.capitalize(displayItem.toString().replace("_", " ")), displayItem);
        setDescriptionStr(List.of("Right Click to remove"));
        this.whitelist = whitelist;
    }

    @Override
    public ItemStack onRightClick() {
        whitelist.remove(getItem().getType());
        getContainer().removeItem(getSlot());
        return null;
    }
}
