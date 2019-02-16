package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;

public class MenuItemScoreboardSave extends MenuItem {

    private ScoreboardDisplay disp;

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
