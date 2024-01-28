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

public class PlayerHealthRangeCondition extends ConditionInterface {
    private final IntegerFlag minHealth = new IntegerFlag(20, "min");
    private final IntegerFlag maxHealth = new IntegerFlag(20, "max");

    @Override
    public String getName() {
        return "PLAYER_HEALTH_RANGE";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Health", minHealth.getFlag() + " - " + maxHealth.getFlag());
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
        return player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
                player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
                player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        minHealth.saveValue(path, config);
        maxHealth.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        minHealth.loadValue(path, config);
        maxHealth.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Health Range", player);
        m.addItem(minHealth.getMenuItem("Min Health", Material.STONE_SLAB, 0, 20));

        m.addItem(maxHealth.getMenuItem("Max Health", Material.STONE, 0, 20));
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
