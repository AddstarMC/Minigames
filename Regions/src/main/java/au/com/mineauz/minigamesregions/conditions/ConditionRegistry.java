package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.menuitems.MenuItemCondition;
import au.com.mineauz.minigamesregions.menuitems.MenuItemConditionAdd;
import org.apache.commons.text.WordUtils;
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

    public static boolean hasCondition(String condition) {
        return conditions.containsKey(condition.toUpperCase());
    }

    public static @Nullable ACondition getConditionByName(@NotNull String name) {
        if (hasCondition(name.toUpperCase())) {
            return conditions.get(name.toUpperCase()).makeNewCondition();
        }
        return null;
    }

    public static Set<ACondition> getAllConditions() {
        return conditions.values().stream().map(ConditionFactory::makeNewCondition).collect(Collectors.toSet());
    }

    public static void displayMenu(MinigamePlayer player, RegionExecutor exec, Menu prev) {
        Menu m = new Menu(3, "ConditionRegistry", player);
        m.setPreviousPage(prev);
        for (ACondition con : exec.getConditions()) {
            m.addItem(new MenuItemCondition(WordUtils.capitalize(con.getName()), Material.PAPER, exec, con));
        }
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(new MenuItemConditionAdd("Add Condition", MenuUtility.getCreateMaterial(), exec), m.getSize() - 1);
        m.displayMenu(player);
    }

    public static void displayMenu(MinigamePlayer player, NodeExecutor exec, Menu prev) {
        Menu m = new Menu(3, "ConditionRegistry", player);
        m.setPreviousPage(prev);
        for (ACondition con : exec.getConditions()) {
            m.addItem(new MenuItemCondition(WordUtils.capitalize(con.getName()), Material.PAPER, exec, con));
        }
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(new MenuItemConditionAdd("Add Condition", MenuUtility.getCreateMaterial(), exec), m.getSize() - 1);
        m.displayMenu(player);
    }


}
