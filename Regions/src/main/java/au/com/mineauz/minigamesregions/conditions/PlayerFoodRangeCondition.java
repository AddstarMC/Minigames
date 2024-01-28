package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerFoodRangeCondition extends ConditionInterface {
    private final IntegerFlag min = new IntegerFlag(20, "min");
    private final IntegerFlag max = new IntegerFlag(20, "max");

    @Override
    public String getName() {
        return "PLAYER_FOOD_RANGE";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Food", min.getFlag() + " - " + max.getFlag());
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
        return checkCondition(player);
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        return checkCondition(player);
    }

    private boolean checkCondition(MinigamePlayer player) {
        return player != null && player.isInMinigame();
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
        Menu m = new Menu(3, "XP Range", player);
        m.addItem(min.getMenuItem("Min Food", Material.STONE_SLAB, 0, 20));
        m.addItem(max.getMenuItem("Max Food", Material.STONE, 0, 20));
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}
