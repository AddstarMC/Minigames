package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuItemRegionExecutorAdd extends MenuItem {
    private final @NotNull Region region;

    public MenuItemRegionExecutorAdd(@Nullable Component name, @Nullable Material displayItem, @NotNull Region region) {
        super(name, displayItem);
        this.region = region;
    }

    public MenuItemRegionExecutorAdd(@Nullable Component name, @Nullable List<@NotNull Component> description, @Nullable Material displayItem, @NotNull Region region) {
        super(name, description, displayItem);
        this.region = region;
    }

    @Override
    public ItemStack onClick() {
        Menu m = new Menu(6, "Select Trigger", getContainer().getViewer());

        List<String> triggers = new ArrayList<>(Triggers.getAllRegionTriggers());
        Collections.sort(triggers);

        for (String trig : triggers) {
            m.addItem(new MenuItemTrigger(Triggers.getTrigger(trig), region, getContainer()));
        }

        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);

        m.displayMenu(getContainer().getViewer());

        return null;
    }
}
