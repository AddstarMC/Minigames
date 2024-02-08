package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import au.com.mineauz.minigamesregions.menuitems.MenuItemAction;
import au.com.mineauz.minigamesregions.menuitems.MenuItemActionAdd;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionRegistry {
    private static final @NotNull Map<@NotNull String, @NotNull ActionFactory> actions = new HashMap<>();

    static {
        for (ActionFactory factory : RegionActions.values()) {
            addAction(factory);
        }
    }

    public static void addAction(ActionFactory factory) {
        actions.put(factory.getName(), factory);
    }

    public static ActionInterface getActionByName(String name) {
        if (actions.containsKey(name.toUpperCase())) {
            return actions.get(name.toUpperCase()).makeNewAction();
        }
        return null;
    }

    public static Set<ActionFactory> getAllActionFactorys() {
        return new HashSet<>(actions.values());
    }

    public static Set<String> getAllActionNames() {
        return actions.keySet();
    }

    public static boolean hasAction(String name) {
        return actions.containsKey(name.toUpperCase());
    }

    public static void displayMenu(MinigamePlayer player, BaseExecutor exec, Menu prev) {
        Menu m = new Menu(3, "Actions", player);
        m.setPreviousPage(prev);
        for (ActionInterface act : exec.getActions()) {
            m.addItem(new MenuItemAction(Material.PAPER, act.getDisplayname(), exec, act));
        }
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(new MenuItemActionAdd(MenuUtility.getCreateMaterial(), "Add Action", exec), m.getSize() - 1);
        m.displayMenu(player);
    }
}
