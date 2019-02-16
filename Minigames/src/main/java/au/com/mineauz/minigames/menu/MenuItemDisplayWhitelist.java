package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayWhitelist extends MenuItem {

    private List<Material> whitelist;
    private Callback<Boolean> whitelistMode;

    public MenuItemDisplayWhitelist(String name, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode) {
        super(name, displayItem);
        this.whitelist = whitelist;
        this.whitelistMode = whitelistMode;
    }

    public MenuItemDisplayWhitelist(String name, List<String> description, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode) {
        super(name, description, displayItem);
        this.whitelist = whitelist;
        this.whitelistMode = whitelistMode;
    }

    @Override
    public ItemStack onClick() {
        Menu menu = new Menu(6, "Block Whitelist", getContainer().getViewer());
        List<MenuItem> items = new ArrayList<>();
        for (Material bl : whitelist) {
            items.add(new MenuItemWhitelistBlock(bl, whitelist));
        }
        menu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), getContainer()), menu.getSize() - 9);
        menu.addItem(new MenuItemAddWhitelistBlock("Add Material", whitelist), menu.getSize() - 1);
        menu.addItem(new MenuItemBoolean("Whitelist Mode", MinigameUtils.stringToList("If whitelist mode only;added items can be;broken."),
                Material.ENDER_PEARL, whitelistMode), menu.getSize() - 2);
        menu.addItems(items);
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
