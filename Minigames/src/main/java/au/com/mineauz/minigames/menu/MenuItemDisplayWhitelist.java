package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayWhitelist extends MenuItem {

    private final List<Material> whitelist;
    private final Callback<Boolean> whitelistMode;
    private final List<String> modeDescription;

    public MenuItemDisplayWhitelist(Component name, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode, List<String> modeDescription) {
        super(name, displayItem);
        this.whitelist = whitelist;
        this.whitelistMode = whitelistMode;
        this.modeDescription = modeDescription;
    }

    public MenuItemDisplayWhitelist(Component name, List<Component> mainDescription, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode, List<String> modeDescription) {
        super(name, mainDescription, displayItem);
        this.whitelist = whitelist;
        this.whitelistMode = whitelistMode;
        this.modeDescription = modeDescription;
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
        menu.addItem(new MenuItemBoolean("Whitelist Mode", modeDescription,
                Material.ENDER_PEARL, whitelistMode), menu.getSize() - 2);
        menu.addItems(items);
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
