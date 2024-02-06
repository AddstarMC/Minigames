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

public class MenuItemToolTeam extends MenuItemList {
    private final Callback<String> value;

    public MenuItemToolTeam(@Nullable Component name, @Nullable Material displayItem, @NotNull Callback<String> value,
                            @NotNull List<@NotNull String> options) {
        super(name, displayItem, value, options);
        this.value = value;
    }

    @Override
    public ItemStack onClick() {
        super.onClick();
        MinigamePlayer ply = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(ply)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(ply);
            tool.setTeam(TeamColor.matchColor(value.getValue().replace(" ", "_")));
        }
        return getItem();
    }

    @Override
    public ItemStack onRightClick() {
        super.onRightClick();
        MinigamePlayer ply = getContainer().getViewer();
        if (MinigameUtils.hasMinigameTool(ply)) {
            MinigameTool tool = MinigameUtils.getMinigameTool(ply);
            tool.setTeam(TeamColor.matchColor(value.getValue().replace(" ", "_")));
        }
        return getItem();
    }
}
