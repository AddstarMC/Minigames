package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemNode extends MenuItem {
    private final Node node;
    private final RegionModule rmod;

    public MenuItemNode(@Nullable Component name, @Nullable Material displayItem, @NotNull Node node, @NotNull RegionModule rmod) {
        super(name, displayItem);
        this.node = node;
        this.rmod = rmod;
    }

    public MenuItemNode(@Nullable Component name, @Nullable List<@NotNull Component> description, @Nullable Material displayItem, @NotNull Node node, @NotNull RegionModule rmod) {
        super(name, description, displayItem);
        this.node = node;
        this.rmod = rmod;
    }

    public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Node node) {
        Menu m = new Menu(3, "Node: " + node.getName(), viewer);
        m.setPreviousPage(previousPage);
        List<MenuItem> items = new ArrayList<>();
        for (NodeExecutor ex : node.getExecutors()) {
            items.add(new MenuItemNodeExecutor(node, ex));
        }
        if (previousPage != null) {
            m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previousPage), m.getSize() - 9);
        }
        m.addItem(new MenuItemNodeExecutorAdd("Add Executor", MenuUtility.getCreateMaterial(), node), m.getSize() - 1);
        m.addItems(items);

        return m;
    }

    @Override
    public ItemStack onClick() {
        Menu m = createMenu(getContainer().getViewer(), getContainer(), node);
        m.displayMenu(getContainer().getViewer());
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        rmod.removeNode(node.getName());
        getContainer().removeItem(getSlot());
        return null;
    }

}
