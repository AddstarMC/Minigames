package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemToolMode extends MenuItem {
    private final ToolMode mode;

    public MenuItemToolMode(@Nullable Material displayMat, @Nullable Component name, @NotNull ToolMode mode) {
        super(displayMat, name);
        this.mode = mode;
    }

    public MenuItemToolMode(@Nullable Material displayMat, @Nullable Component name,
                            @Nullable List<@NotNull Component> description, @NotNull ToolMode mode) {
        super(displayMat, name, description);
        this.mode = mode;
    }

    public ItemStack onClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(mgPlayer)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(mgPlayer);
            if (tool.getMode() != null) {
                tool.getMode().onUnsetMode(mgPlayer, tool);
            }
            tool.setMode(mode);
            tool.getMode().onSetMode(mgPlayer, tool);
        }
        return getItem();
    }
}
