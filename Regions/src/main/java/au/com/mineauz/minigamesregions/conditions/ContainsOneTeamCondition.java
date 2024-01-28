package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ContainsOneTeamCondition extends ConditionInterface {

    @Override
    public String getName() {
        return "CONTAINS_ONE_TEAM";
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
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return false;
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        boolean ret = true;
        Team last = player.getTeam();
        if (last == null) return true;
        for (MinigamePlayer p : region.getPlayers()) {
            if (last != p.getTeam()) {
                ret = false;
                break;
            }
        }
        return ret;
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
        Menu m = new Menu(3, "Contains One Team", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return false;
    }
}
