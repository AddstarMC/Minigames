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

public class PlayerHealthRangeCondition extends ACondition {
    private final IntegerFlag minHealth = new IntegerFlag(20, "min");
    private final IntegerFlag maxHealth = new IntegerFlag(20, "max");

    protected PlayerHealthRangeCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_PLAYERHEALTHRANGE_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.PLAYER;
    }

    @Override
    public void describe(Map<String, Object> out) {
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
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
                player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        if (player == null || !player.isInMinigame()) return false;
        return player.getPlayer().getHealth() >= minHealth.getFlag().doubleValue() &&
                player.getPlayer().getHealth() <= maxHealth.getFlag().doubleValue();
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        minHealth.saveValue(path, config);
        maxHealth.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        minHealth.loadValue(path, config);
        maxHealth.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(minHealth.getMenuItem(Material.STONE_SLAB, RegionMessageManager.getMessage(RegionLangKey.MENU_HEALTH_MINIMUM_NAME), 0, 20));
        m.addItem(maxHealth.getMenuItem(Material.STONE, RegionMessageManager.getMessage(RegionLangKey.MENU_HEALTH_MAXIMUM_NAME), 0, 20));
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean onPlayerApplicable() {
        return true;
    }
}
