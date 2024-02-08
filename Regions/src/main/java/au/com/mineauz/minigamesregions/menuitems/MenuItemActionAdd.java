package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.actions.ActionFactory;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.ActionRegistry;
import au.com.mineauz.minigamesregions.actions.IActionCategory;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuItemActionAdd extends MenuItem {
    private final @NotNull BaseExecutor exec;

    public MenuItemActionAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull BaseExecutor exec) {
        super(displayMat, name);
        this.exec = exec;
    }

    @Override
    public ItemStack onClick() { // miau
        Menu m = new Menu(6, RegionMessageManager.getMessage(RegionLangKey.MENU_ACTIONS_NAME), getContainer().getViewer());
        m.setPreviousPage(getContainer());
        Map<IActionCategory, Menu> cats = new HashMap<>();
        List<ActionFactory> acts = new ArrayList<>(ActionRegistry.getAllActionFactorys());
        for (ActionFactory factory : acts) {
            final ActionInterface action = factory.makeNewAction();
            if (action.useInNodes() || action.useInRegions()) {
                IActionCategory category = action.getCategory();
                Menu menuCat;
                if (!cats.containsKey(category)) {
                    menuCat = new Menu(6, category.getDisplayName(), getContainer().getViewer());
                    cats.put(category, menuCat);
                    m.addItem(new MenuItemPage(Material.CHEST, category.getDisplayName(), menuCat));
                    menuCat.addItem(new MenuItemBack(m), menuCat.getSize() - 9);
                } else {
                    menuCat = cats.get(category);
                }

                MenuItemCustom menuItemCustom = new MenuItemCustom(Material.PAPER, action.getDisplayname());
                menuItemCustom.setClick(object -> {
                    exec.addAction(action);
                    getContainer().addItem(new MenuItemAction(Material.PAPER, action.getDisplayname(), exec, action));
                    getContainer().displayMenu(getContainer().getViewer());
                    return null;
                });
                menuCat.addItem(menuItemCustom);
            }
        }
        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
        m.displayMenu(getContainer().getViewer());
        return null;
    }
}
