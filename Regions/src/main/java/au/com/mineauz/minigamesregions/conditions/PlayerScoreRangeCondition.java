package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
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

public class PlayerScoreRangeCondition extends ACondition {
    private final IntegerFlag min = new IntegerFlag(5, "min");
    private final IntegerFlag max = new IntegerFlag(10, "max");

    protected PlayerScoreRangeCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_PLAYERSCORERANGE_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.PLAYER;
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Score", min.getFlag() + " - " + max.getFlag());
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
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getScore() >= min.getFlag() && player.getScore() <= max.getFlag();
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getScore() >= min.getFlag() && player.getScore() <= max.getFlag();
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
        max.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(min.getMenuItem(Material.STONE_SLAB, RegionMessageManager.getMessage(RegionLangKey.MENU_SCORE_MINIMUM_NAME), 0, null));
        m.addItem(max.getMenuItem(Material.STONE, RegionMessageManager.getMessage(RegionLangKey.MENU_SCORE_MAXIMUM_NAME), 0, null));
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}
