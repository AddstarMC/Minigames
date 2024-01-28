package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HasRequiredFlagsCondition extends ConditionInterface {

    @Override
    public String getName() {
        return "HAS_REQUIRED_FLAGS";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return Minigames.getPlugin().getPlayerManager().checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty();
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        if (player == null || !player.isInMinigame()) return false;
        return Minigames.getPlugin().getPlayerManager().checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty();
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Required Flags", player);
        addInvertMenuItem(m);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}
