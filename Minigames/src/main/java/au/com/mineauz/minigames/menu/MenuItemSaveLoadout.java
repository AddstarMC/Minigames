package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.PlayerLoadout;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemSaveLoadout extends MenuItem {

    private PlayerLoadout loadout = null;
    private Menu altMenu = null;

    public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout) {
        super(name, displayItem);
        this.loadout = loadout;
    }

    public MenuItemSaveLoadout(String name, List<String> description, Material displayItem, PlayerLoadout loadout) {
        super(name, description, displayItem);
        this.loadout = loadout;
    }

    public MenuItemSaveLoadout(String name, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
        super(name, displayItem);
        this.loadout = loadout;
        this.altMenu = altMenu;
    }

    public MenuItemSaveLoadout(String name, List<String> description, Material displayItem, PlayerLoadout loadout, Menu altMenu) {
        super(name, description, displayItem);
        this.loadout = loadout;
        this.altMenu = altMenu;
    }

    @Override
    public ItemStack onClick() {
        ItemStack[] items = getContainer().getInventory();
        loadout.clearLoadout();

        for (int i = 0; i < 36; i++) {
            if (items[i] != null)
                loadout.addItem(items[i], i);
        }
        int a = 40;
        if (loadout.allowOffHand()) {
            a = 41;
        }
        for (int i = 36; i < a; i++) {
            if (items[i] != null) {
                switch (i) {
                    case 36:
                        loadout.addItem(items[i], 103);
                        break;
                    case 37:
                        loadout.addItem(items[i], 102);
                        break;
                    case 38:
                        loadout.addItem(items[i], 101);
                        break;
                    case 39:
                        loadout.addItem(items[i], 100);
                        break;
                    case 40:
                        loadout.addItem(items[i], -106);
                        break;
                }
            }
        }
        getContainer().getViewer().sendMessage("Saved the '" + loadout.getName(false) + "' loadout.", MinigameMessageType.INFO);
        if (altMenu == null)
            getContainer().getPreviousPage().displayMenu(getContainer().getViewer());
        else
            altMenu.displayMenu(getContainer().getViewer());
        return null;
    }
}
