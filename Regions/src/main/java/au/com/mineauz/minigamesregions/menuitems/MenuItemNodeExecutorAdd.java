package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuItemNodeExecutorAdd extends MenuItem {
    private final Node node;

    public MenuItemNodeExecutorAdd(@Nullable Component name, @Nullable Material displayItem, @NotNull Node node) {
        super(name, displayItem);
        this.node = node;
    }

    public MenuItemNodeExecutorAdd(@Nullable Component name, @Nullable List<@NotNull Component> description,
                                   @Nullable Material displayItem, @NotNull Node node) {
        super(name, description, displayItem);
        this.node = node;
    }

    @Override
    public ItemStack onClick() {
        Menu m = new Menu(6, "Select Trigger", getContainer().getViewer());

        List<String> triggers = new ArrayList<>(Triggers.getAllNodeTriggers());
        Collections.sort(triggers);

        for (String trig : triggers) {
            m.addItem(new MenuItemTrigger(Triggers.getTrigger(trig), node, getContainer()));
        }

        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);

        m.displayMenu(getContainer().getViewer());

        return null;
    }
}
