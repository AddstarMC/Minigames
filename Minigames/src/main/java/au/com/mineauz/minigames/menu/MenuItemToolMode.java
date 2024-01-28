package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemToolMode extends MenuItem {

    private final ToolMode mode;

    public MenuItemToolMode(Component name, Material displayItem, ToolMode mode) {
        super(name, displayItem);
        this.mode = mode;
    }

    public MenuItemToolMode(Component name, List<Component> description, Material displayItem, ToolMode mode) {
        super(name, description, displayItem);
        this.mode = mode;
    }

    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(ply)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(ply);
            if (tool.getMode() != null) {
                tool.getMode().onUnsetMode(ply, tool);
            }
            tool.setMode(mode);
            tool.getMode().onSetMode(ply, tool);
        }
        return getDisplayItem();
    }
}
