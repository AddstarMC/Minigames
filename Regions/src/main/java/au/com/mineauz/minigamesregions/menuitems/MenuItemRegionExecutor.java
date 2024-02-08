package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.actions.ActionRegistry;
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
        super(Material.ENDER_PEARL, RegionMessageManager.getMessage(RegionLangKey.MENU_REGIONEXECUTOR_NAME));
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
        final Menu menu = new Menu(3, RegionMessageManager.getMessage(RegionLangKey.MENU_REGIONEXECUTOR_NAME), fviewer);

        MenuItemCustom ca = new MenuItemCustom(Material.CHEST,
                RegionMessageManager.getMessage(RegionLangKey.MENU_ACTIONS_NAME));
        ca.setClick(() -> {
            ActionRegistry.displayMenu(fviewer, ex, menu);
            return null;
        });
        menu.addItem(ca);

        MenuItemCustom c2 = new MenuItemCustom(Material.CHEST,
                RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITIONS_NAME));
        c2.setClick(() -> {
            ConditionRegistry.displayMenu(fviewer, ex, menu);
            return null;
        });
        menu.addItem(c2);

        menu.addItem(new MenuItemNewLine());
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            menu.addItem(new MenuItemInteger(Material.STONE,
                    RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_TRIGGERCOUNT_NAME),
                    List.of("Number of times this", "executor can be", "triggered"),
                    ex.getTriggerCountCallback(), 0, null));
        }
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            menu.addItem(new MenuItemBoolean(List.of("Whether this executor", "is triggered per player", "or just on count"), Material.PLAYER_HEAD,
                    RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_PERPLAYER_NAME),
                    ex.getIsTriggerPerPlayerCallback()));
        }
        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.displayMenu(fviewer);
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        region.removeExecutor(ex);
        getContainer().removeItem(getSlot());
        return null;
    }
}
