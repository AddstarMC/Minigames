package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TeamPlayerCountCondition extends ACondition {
    private final IntegerFlag min = new IntegerFlag(1, "min");
    private final IntegerFlag max = new IntegerFlag(5, "max");

    protected TeamPlayerCountCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_TEAMPLAYERCOUNT_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.TEAM;
    }

    @Override
    public void describe(Map<String, Object> out) {
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
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
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
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return false;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        min.saveValue(path, config);
        max.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        min.loadValue(path, config);
        max.saveValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(min.getMenuItem(Material.STONE_SLAB, RegionMessageManager.getMessage(RegionLangKey.MENU_PLAYERCOUNT_MINIMUM_NAME), 1, null));
        m.addItem(max.getMenuItem(Material.STONE, RegionMessageManager.getMessage(RegionLangKey.MENU_PLAYERCOUNT_MAXIMUM_NAME), 1, null));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean onPlayerApplicable() {
        return true;
    }
}
