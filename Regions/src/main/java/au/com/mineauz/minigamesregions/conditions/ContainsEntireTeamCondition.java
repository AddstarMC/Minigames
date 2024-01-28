package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

public class ContainsEntireTeamCondition extends ConditionInterface {

    @Override
    public String getName() {
        return "CONTAINS_ENTIRE_TEAM";
    }

    @Override
    public String getCategory() {
        return "Team Conditions";
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
        return false;
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        return new HashSet<>(region.getPlayers()).containsAll(player.getTeam().getPlayers());
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return false;
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu menu = new Menu(3, "Contains Entire Team", player);
        menu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), menu.getSize() - 9);
        addInvertMenuItem(menu);
        menu.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}
