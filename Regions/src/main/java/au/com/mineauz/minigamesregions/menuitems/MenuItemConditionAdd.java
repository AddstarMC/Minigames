package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.conditions.ACondition;
import au.com.mineauz.minigamesregions.conditions.ConditionRegistry;
import au.com.mineauz.minigamesregions.conditions.IConditionCategory;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MenuItemConditionAdd extends MenuItem {
    private final @Nullable RegionExecutor rexec;
    private final @Nullable NodeExecutor nexec;

    public MenuItemConditionAdd(@Nullable Material displayMat, @NotNull Component name, @NotNull RegionExecutor exec) {
        super(displayMat, name);
        this.rexec = exec;
        this.nexec = null;
    }

    public MenuItemConditionAdd(@Nullable Material displayMat, @NotNull Component name, @NotNull NodeExecutor exec) {
        super(displayMat, name);
        this.rexec = null;
        this.nexec = exec;
    }

    @Override
    public ItemStack onClick() {
        Menu menu = new Menu(6, RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITIONS_NAME), getContainer().getViewer());
        menu.setPreviousPage(getContainer());
        Map<IConditionCategory, Menu> cats = new HashMap<>();
        List<ACondition> cons = new ArrayList<>(ConditionRegistry.getAllConditions());
        for (ACondition condition : cons) {
            if ((condition.useInNodes() && nexec != null) || (condition.useInRegions() && rexec != null)) {
                if (!Objects.requireNonNullElse(rexec, nexec).getTrigger().triggerOnPlayerAvailable()) {
                    if (condition.onPlayerApplicable()) {
                        continue;
                    }
                }

                IConditionCategory category = condition.getCategory();
                Menu catMenu;
                if (!cats.containsKey(category)) {
                    catMenu = new Menu(6, category.getDisplayName(), getContainer().getViewer());
                    cats.put(category, catMenu);
                    menu.addItem(new MenuItemPage(Material.CHEST, category.getDisplayName(), catMenu));
                    catMenu.addItem(new MenuItemBack(menu), catMenu.getSize() - 9);
                } else
                    catMenu = cats.get(category);
                MenuItemCustom menuItemCustom = new MenuItemCustom(Material.PAPER, condition.getDisplayName());

                menuItemCustom.setClick(() -> {
                    if (rexec != null) {
                        rexec.addCondition(condition);
                        getContainer().addItem(new MenuItemCondition(Material.PAPER, condition.getDisplayName(), rexec, condition));
                    } else {
                        nexec.addCondition(condition);
                        getContainer().addItem(new MenuItemCondition(Material.PAPER, condition.getDisplayName(), nexec, condition));
                    }
                    getContainer().displayMenu(getContainer().getViewer());
                    return null;
                });
                catMenu.addItem(menuItemCustom);
            }
        }
        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.displayMenu(getContainer().getViewer());
        return null;
    }
}
