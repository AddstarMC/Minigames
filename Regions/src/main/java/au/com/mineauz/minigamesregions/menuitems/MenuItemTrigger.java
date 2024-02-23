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

public class MenuItemTrigger extends MenuItem {

    private final Trigger trigger;
    private final Menu previous;
    private Region region;
    private Node node;

    public MenuItemTrigger(Trigger trigger, Region region, Menu previous) {
        super(WordUtils.capitalize(trigger.getName().replace("_", " ")), Material.LEVER);
        this.trigger = trigger;
        this.region = region;
        this.previous = previous;
    }

    public MenuItemTrigger(Trigger trigger, Node node, Menu previous) {
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
