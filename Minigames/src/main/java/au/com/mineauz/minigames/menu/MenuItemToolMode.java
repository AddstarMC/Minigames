package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;

public class MenuItemToolMode extends MenuItem {

    private ToolMode mode;

    public MenuItemToolMode(String name, Material displayItem, ToolMode mode) {
        super(name, displayItem);
        this.mode = mode;
    }

    public MenuItemToolMode(String name, List<String> description, Material displayItem, ToolMode mode) {
        super(name, description, displayItem);
        this.mode = mode;
    }

    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(ply)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(ply);
            if (tool.getMode() != null)
                tool.getMode().onUnsetMode(ply, tool);
            tool.setMode(mode);
            tool.getMode().onSetMode(ply, tool);
        }
        return getItem();
    }
}
