package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

public class ContainsEntireTeamCondition extends ACondition {

    protected ContainsEntireTeamCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_CONTAINSENTIRETEAM_NAME);
    }

    @Override
    public String getCategory() {
        return "Team ConditionRegistry";
    }

    @Override
    public void describe(Map<String, Object> out) {
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
        return new HashSet<>(region.getPlayers()).containsAll(player.getTeam().getPlayers());
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return false;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu menu = new Menu(3, "Contains Entire Team", player);
        menu.addItem(new MenuItemBack(prev), menu.getSize() - 9);
        addInvertMenuItem(menu);
        menu.displayMenu(player);
        return true;
    }

    @Override
    public boolean onPlayerApplicable() {
        return true;
    }
}
