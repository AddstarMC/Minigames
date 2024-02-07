package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.conditions.ACondition;
import au.com.mineauz.minigamesregions.conditions.ConditionRegistry;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
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
        Menu m = new Menu(6, "ConditionRegistry", getContainer().getViewer());
        m.setPreviousPage(getContainer());
        Map<String, Menu> cats = new HashMap<>();
        List<ACondition> cons = new ArrayList<>(ConditionRegistry.getAllConditions());
        for (ACondition condition : cons) {
            if ((condition.useInNodes() && nexec != null) || (condition.useInRegions() && rexec != null)) {
                if (!Objects.requireNonNullElse(rexec, nexec).getTrigger().triggerOnPlayerAvailable()) {
                    if (condition.onPlayerApplicable()) {
                        continue;
                    }
                }
                String catname = condition.getCategory();
                if (catname == null) {
                    catname = "misc conditions";
                }
                catname = catname.toLowerCase();
                Menu cat;
                if (!cats.containsKey(catname)) {
                    cat = new Menu(6, WordUtils.capitalize(catname), getContainer().getViewer());
                    cats.put(catname, cat);
                    m.addItem(new MenuItemPage(Material.CHEST, WordUtils.capitalize(catname), cat));
                    cat.addItem(new MenuItemBack(m), cat.getSize() - 9);
                } else
                    cat = cats.get(catname);
                MenuItemCustom menuItemCustom = new MenuItemCustom(Material.PAPER, condition.getDisplayName());

                menuItemCustom.setClick(object -> {
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
                cat.addItem(menuItemCustom);
            }
        }
        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
        m.displayMenu(getContainer().getViewer());
        return null;
    }

}
