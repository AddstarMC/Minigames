package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.ConditionRegistry;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemRegionExecutor extends MenuItem {
    private final @NotNull Region region;
    private final @NotNull RegionExecutor ex;

    public MenuItemRegionExecutor(@NotNull Region region, @NotNull RegionExecutor ex) {
        super(Material.ENDER_PEARL, "Region Executor:");
        this.region = region;
        this.ex = ex;
        setDescription(List.of(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY +
                        ex.getTrigger().getDisplayName(),
                ChatColor.GREEN + "Actions: " + ChatColor.GRAY +
                        ex.getActions().size(),
                ChatColor.DARK_PURPLE + "(Right click to delete)",
                "(Left click to edit)"));
    }

    @Override
    public ItemStack onClick() {
        final MinigamePlayer fviewer = getContainer().getViewer();
        Menu m = new Menu(3, RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_NAME), fviewer);
        final Menu ffm = m;

        MenuItemCustom ca = new MenuItemCustom(Material.CHEST, "Actions");
        ca.setClick(object -> {
            Actions.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(ca);

        MenuItemCustom c2 = new MenuItemCustom(Material.CHEST, "ConditionRegistry");
        c2.setClick(object -> {
            ConditionRegistry.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(c2);

        m.addItem(new MenuItemNewLine());
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            m.addItem(new MenuItemInteger(Material.STONE, "Trigger Count",
                    List.of("Number of times this", "node can be", "triggered"),
                    ex.getTriggerCountCallback(), 0, null));
        }
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            m.addItem(new MenuItemBoolean("Trigger Per Player",
                    List.of("Whether this node", "is triggered per player", "or just on count"),
                    Material.ENDER_PEARL, ex.getIsTriggerPerPlayerCallback()));
        }
        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
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
