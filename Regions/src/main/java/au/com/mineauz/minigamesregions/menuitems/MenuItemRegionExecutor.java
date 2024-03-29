package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.Conditions;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemRegionExecutor extends MenuItem {
    private final Region region;
    private final RegionExecutor ex;

    public MenuItemRegionExecutor(Region region, RegionExecutor ex) {
        super("Region Executor:", Material.ENDER_PEARL);
        this.region = region;
        this.ex = ex;
        setDescription(List.of(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY +
                        WordUtils.capitalize(ex.getTrigger().getName()),
                ChatColor.GREEN + "Actions: " + ChatColor.GRAY +
                        ex.getActions().size(),
                ChatColor.DARK_PURPLE + "(Right click to delete)",
                "(Left click to edit)"));
    }

    @Override
    public ItemStack onClick() {
        final MinigamePlayer fviewer = getContainer().getViewer();
        Menu m = new Menu(3, "Executor", fviewer);
        final Menu ffm = m;

        MenuItemCustom ca = new MenuItemCustom("Actions", Material.CHEST);
        ca.setClick(object -> {
            Actions.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(ca);

        MenuItemCustom c2 = new MenuItemCustom("Conditions", Material.CHEST);
        c2.setClick(object -> {
            Conditions.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(c2);

        m.addItem(new MenuItemNewLine());
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            m.addItem(new MenuItemInteger("Trigger Count",
                    List.of("Number of times this", "node can be", "triggered"),
                    Material.STONE, ex.getTriggerCountCallback(), 0, null));
        }
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            m.addItem(new MenuItemBoolean("Trigger Per Player",
                    List.of("Whether this node", "is triggered per player", "or just on count"),
                    Material.ENDER_PEARL, ex.getIsTriggerPerPlayerCallback()));
        }
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), getContainer()), m.getSize() - 9);
        m.displayMenu(fviewer);
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        region.removeExecutor(ex);
        getContainer().removeItem(getSlot());
        return null;
    }

}
