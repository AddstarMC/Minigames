package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuItemTrigger extends MenuItem {
    private final @NotNull Trigger trigger;
    private final @NotNull Menu previous;
    private @Nullable Region region;
    private @NotNull Node node;

    public MenuItemTrigger(@NotNull Trigger trigger, @NotNull Region region, @NotNull Menu previous) {
        super(WordUtils.capitalize(trigger.getName().replace("_", " ")), Material.LEVER);
        this.trigger = trigger;
        this.region = region;
        this.previous = previous;
    }

    public MenuItemTrigger(@NotNull Trigger trigger, @NotNull Node node, @NotNull Menu previous) {
        super(WordUtils.capitalize(trigger.getName().replace("_", " ")), Material.LEVER);
        this.trigger = trigger;
        this.node = node;
        this.previous = previous;
    }

    @Override
    public ItemStack onClick() {
        if (region != null) {
            RegionExecutor exec = new RegionExecutor(trigger);
            region.addExecutor(exec);
            previous.addItem(new MenuItemRegionExecutor(region, exec));
            previous.displayMenu(getContainer().getViewer());
        } else {
            NodeExecutor exec = new NodeExecutor(trigger);
            node.addExecutor(exec);
            previous.addItem(new MenuItemNodeExecutor(node, exec));
            previous.displayMenu(getContainer().getViewer());
        }
        return null;
    }

}
