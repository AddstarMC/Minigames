package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemToolTeam extends MenuItemList<TeamColor> {
    private final Callback<TeamColor> value;

    public MenuItemToolTeam(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<TeamColor> value,
                            @NotNull List<@NotNull TeamColor> options) {
        super(displayMat, name, value, options);
        this.value = value;
    }

    @Override
    public ItemStack onClick() {
        super.onClick();
        MinigamePlayer mgPlayer = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(mgPlayer)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(mgPlayer);
            tool.setTeam(value.getValue());
        }
        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        super.onRightClick();
        MinigamePlayer mgPlayer = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(mgPlayer)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(mgPlayer);
            tool.setTeam(value.getValue());
        }
        return getDisplayItem();
    }
}
