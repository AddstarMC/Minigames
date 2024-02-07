package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MenuItemActionAdd extends MenuItem {
    private final @NotNull BaseExecutor exec;

    public MenuItemActionAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull BaseExecutor exec) {
        super(displayMat, name);
        this.exec = exec;
    }

    @Override
    public ItemStack onClick() {
        Menu m = new Menu(6, "Actions", getContainer().getViewer());
        m.setPreviousPage(getContainer());
        Map<String, Menu> cats = new HashMap<>();
        List<String> acts = new ArrayList<>(Actions.getAllActionNames());
        Collections.sort(acts);
        for (String act : acts) {
            if (Actions.getActionByName(act).useInNodes() || Actions.getActionByName(act).useInRegions()) {
                String catname = Actions.getActionByName(act).getCategory();
                if (catname == null) catname = "misc actions";
                catname = catname.toLowerCase();
                Menu cat;
                if (!cats.containsKey(catname)) {
                    cat = new Menu(6, WordUtils.capitalize(catname), getContainer().getViewer());
                    cats.put(catname, cat);
                    m.addItem(new MenuItemPage(WordUtils.capitalize(catname), Material.CHEST, cat));
                    cat.addItem(new MenuItemBack(m), cat.getSize() - 9);
                } else
                    cat = cats.get(catname);
                MenuItemCustom c = new MenuItemCustom(Material.PAPER, WordUtils.capitalize(act));
                final String fact = act;
                c.setClick(object -> {
                    ActionInterface action = Actions.getActionByName(fact);
                    exec.addAction(action);
                    getContainer().addItem(new MenuItemAction(Material.PAPER, WordUtils.capitalize(fact), exec, action));
                    getContainer().displayMenu(getContainer().getViewer());
                    return null;
                });
                cat.addItem(c);
            }
        }
        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
        m.displayMenu(getContainer().getViewer());
        return null;
    }
}
