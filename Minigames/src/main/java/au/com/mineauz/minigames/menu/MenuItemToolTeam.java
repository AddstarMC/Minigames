package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.tool.MinigameTool;

public class MenuItemToolTeam extends MenuItemList {

    private Callback<String> value;

    public MenuItemToolTeam(String name, Material displayItem, Callback<String> value, List<String> options) {
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
