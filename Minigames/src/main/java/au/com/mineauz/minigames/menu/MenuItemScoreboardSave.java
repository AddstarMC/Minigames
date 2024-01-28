package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemScoreboardSave extends MenuItem {
    private final ScoreboardDisplay disp;

    public MenuItemScoreboardSave(Component name, Material displayItem, ScoreboardDisplay disp) {
        super(name, displayItem);
        this.disp = disp;
    }

    public MenuItemScoreboardSave(Component name, List<Component> description, Material displayItem, ScoreboardDisplay disp) {
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
