package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemScoreboardSave extends MenuItem {
    private final ScoreboardDisplay disp;

    public MenuItemScoreboardSave(@Nullable Material displayMat, @Nullable Component name, @NotNull ScoreboardDisplay disp) {
        super(displayMat, name);
        this.disp = disp;
    }

    public MenuItemScoreboardSave(@Nullable Material displayMat, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull ScoreboardDisplay disp) {
        super(displayMat, name, description);
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
