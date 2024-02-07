package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.FloatFlag;
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

public class PlayerXPRangeCondition extends ACondition {
    private final FloatFlag min = new FloatFlag(1.0f, "min");
    private final FloatFlag max = new FloatFlag(1.0f, "max");

    protected PlayerXPRangeCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_PLAYERXPRANGE_NAME);
    }

    @Override
    public String getCategory() {
        return "Player ConditionRegistry";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Xp", min.getFlag() + " - " + max.getFlag());
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
        return checkCondition(player);
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return checkCondition(player);
    }

    private boolean checkCondition(MinigamePlayer player) {
        return player != null && player.isInMinigame();
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
        max.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "XP Range", player);
        m.addItem(min.getMenuItem("Min XP", Material.STONE_SLAB, 0.5, 1, 0.0, null));
        m.addItem(max.getMenuItem("Max XP", Material.STONE, 0.5, 1, 0.0, null));
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
