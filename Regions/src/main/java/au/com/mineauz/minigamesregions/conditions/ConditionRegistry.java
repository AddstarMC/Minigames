package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.menuitems.MenuItemCondition;
import au.com.mineauz.minigamesregions.menuitems.MenuItemConditionAdd;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConditionRegistry {
    private static final @NotNull Map<@NotNull String, @NotNull ConditionFactory> conditions = new HashMap<>();

    static {
        for (ConditionFactory conditionFactory : RegionConditions.values()) {
            addCondition(conditionFactory);
        }
    }

    public static void addCondition(@NotNull ConditionFactory conditionFactory) {
        conditions.put(conditionFactory.getName(), conditionFactory);
    }

    public static @Nullable ACondition getConditionByName(@NotNull String name) {
        ConditionFactory factory = conditions.get(name.toUpperCase());
        return factory != null ? factory.makeNewCondition() : null;
    }

    public static Set<ACondition> getAllConditions() {
        return conditions.values().stream().map(ConditionFactory::makeNewCondition).collect(Collectors.toSet());
    }

    public static void displayMenu(@NotNull MinigamePlayer mgPlayer, @NotNull RegionExecutor exec, @NotNull Menu prev) {
        Menu m = new Menu(3, "Conditions", mgPlayer);
        m.setPreviousPage(prev);
        for (ACondition con : exec.getConditions()) {
            m.addItem(new MenuItemCondition(Material.PAPER, con.getDisplayName(), exec, con));
        }
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(new MenuItemConditionAdd(MenuUtility.getCreateMaterial(), "Add Condition", exec), m.getSize() - 1);
        m.displayMenu(mgPlayer);
    }

    public static void displayMenu(@NotNull MinigamePlayer mgPlayer, @NotNull NodeExecutor exec, @NotNull Menu prev) {
        Menu m = new Menu(3, "Conditions", mgPlayer);
        m.setPreviousPage(prev);
        for (ACondition con : exec.getConditions()) {
            m.addItem(new MenuItemCondition(Material.PAPER, con.getDisplayName(), exec, con));
        }
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(new MenuItemConditionAdd(MenuUtility.getCreateMaterial(), "Add Condition", exec), m.getSize() - 1);
        m.displayMenu(mgPlayer);
    }
}
