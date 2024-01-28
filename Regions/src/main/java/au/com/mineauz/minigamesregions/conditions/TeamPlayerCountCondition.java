package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TeamPlayerCountCondition extends ConditionInterface {
    private final IntegerFlag min = new IntegerFlag(1, "min");
    private final IntegerFlag max = new IntegerFlag(5, "max");

    @Override
    public String getName() {
        return "TEAM_PLAYER_COUNT";
    }

    @Override
    public String getCategory() {
        return "Team Conditions";
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Count", min.getFlag() + " - " + max.getFlag());
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
        if (player.getTeam() != null) {
            int count = 0;
            Team t = player.getTeam();
            for (MinigamePlayer user : region.getPlayers()) {
                if (user.getTeam().equals(t)) {
                    count++;
                }
            }

            return (count >= min.getFlag() && count <= max.getFlag());
        }
        return false;
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return false;
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        min.saveValue(path, config);
        max.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        min.loadValue(path, config);
        max.saveValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Player Count", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.addItem(min.getMenuItem("Min Player Count", Material.STONE_SLAB, 1, null));
        m.addItem(max.getMenuItem("Max Player Count", Material.STONE, 1, null));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}
