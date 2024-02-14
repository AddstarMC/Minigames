package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.actions.ActionRegistry;
import au.com.mineauz.minigamesregions.conditions.ConditionRegistry;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemNodeExecutor extends MenuItem {
    private final @NotNull Node node;
    private final @NotNull NodeExecutor ex;

    public MenuItemNodeExecutor(@NotNull Node node, @NotNull NodeExecutor ex) {
        super(Material.ENDER_PEARL, RegionMessageManager.getMessage(RegionLangKey.MENU_NODEEXECUTOR_NAME));
        this.node = node;
        this.ex = ex;
        setDescription(List.of(
                ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + ex.getTrigger().getDisplayName(),
                ChatColor.GREEN + "Actions: " + ChatColor.GRAY + ex.getActions().size(),
                ChatColor.DARK_PURPLE + "(Right click to delete)",
                "(Left click to edit)"));
    }

    @Override
    public ItemStack onClick() {
        final MinigamePlayer fviewer = getContainer().getViewer();
        Menu m = new Menu(3, RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_NAME), fviewer);
        final Menu ffm = m;

        MenuItemCustom ca = new MenuItemCustom(Material.CHEST, RegionMessageManager.getMessage(RegionLangKey.MENU_ACTIONS_NAME));
        ca.setClick(() -> {
            ActionRegistry.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(ca);

        MenuItemCustom c2 = new MenuItemCustom(Material.CHEST, RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITIONS_NAME));
        c2.setClick(() -> {
            ConditionRegistry.displayMenu(fviewer, ex, ffm);
            return null;
        });
        m.addItem(c2);

        m.addItem(new MenuItemNewLine());

        m.addItem(new MenuItemInteger(Material.STONE,
                RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_TRIGGERCOUNT_NAME),
                List.of("Number of times this", "executor can be", "triggered"),
                ex.getTriggerCountCallback(), 0, null));

        m.addItem(new MenuItemBoolean(
                Material.ENDER_PEARL,
                RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_PERPLAYER_NAME),
                List.of("Whether this executor", "is triggered per player", "or just on count"),
                ex.getIsTriggerPerPlayerCallback()));
        m.addItem(new MenuItemBack(getContainer()), m.getSize() - 9);
        m.displayMenu(fviewer);
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        node.removeExecutor(ex);
        getContainer().removeItem(getSlot());
        return null;
    }
}
