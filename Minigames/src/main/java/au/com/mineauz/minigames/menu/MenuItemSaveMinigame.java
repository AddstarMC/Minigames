package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemSaveMinigame extends MenuItem {
    private Minigame mgm = null;

    public MenuItemSaveMinigame(String name, Material displayItem, Minigame minigame) {
        super(name, displayItem);
        mgm = minigame;
    }

    public MenuItemSaveMinigame(String name, List<String> description, Material displayItem, Minigame minigame) {
        super(name, description, displayItem);
        mgm = minigame;
    }

    @Override
    public ItemStack onClick() {
        mgm.saveMinigame();
        getContainer().getViewer().sendMessage("Saved the '" + mgm.getName(false) + "' Minigame.", MinigameMessageType.INFO);
        return getItem();
    }

}
