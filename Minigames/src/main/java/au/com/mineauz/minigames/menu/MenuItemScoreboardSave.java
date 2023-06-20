package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemScoreboardSave extends MenuItem {

    private final ScoreboardDisplay disp;

    public MenuItemScoreboardSave(String name, Material displayItem, ScoreboardDisplay disp) {
        super(name, displayItem);
        this.disp = disp;
    }

    public MenuItemScoreboardSave(String name, List<String> description, Material displayItem, ScoreboardDisplay disp) {
        super(name, description, displayItem);
        this.disp = disp;
    }

    @Override
    public ItemStack onClick() {
        disp.placeRootSign();
        disp.getMinigame().getScoreboardData().reload(disp.getRoot().getBlock());

        getContainer().getViewer().getPlayer().closeInventory();
        return null;
    }
}
