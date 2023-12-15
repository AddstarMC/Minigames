package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemSaveMinigame extends MenuItem {
    private final @NotNull Minigame mgm;

    public MenuItemSaveMinigame(String name, Material displayItem, @NotNull Minigame minigame) {
        super(name, displayItem);
        mgm = minigame;
    }

    public MenuItemSaveMinigame(String name, List<@NotNull String> description, Material displayItem, @NotNull Minigame minigame) {
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
