package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MenuItemAction extends MenuItem {
    private final BaseExecutor exec;
    private final ActionInterface act;

    public MenuItemAction(Component name, Material displayItem, BaseExecutor exec, ActionInterface act) {
        super(name, displayItem);
        this.exec = exec;
        this.act = act;
        updateDescription();
    }

    @Override
    public void update() {
        updateDescription();
    }

    private void updateDescription() {
        Map<String, Object> out = new HashMap<>();
        act.describe(out);

        if (out.isEmpty()) {
            return;
        }

        int lineLimit = 35;

        // Convert the description
        List<Component> description = new ArrayList<>();
        for (Entry<String, Object> entry : out.entrySet()) {
            Object value = entry.getValue();
            String line = ChatColor.GRAY + entry.getKey() + ": ";

            // Translate the value
            if (value instanceof Boolean) {
                if (((Boolean) value)) {
                    value = ChatColor.GREEN + "True";
                } else {
                    value = ChatColor.RED + "False";
                }
            } else if (value == null) {
                value = ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Not Set";
            } else {
                value = ChatColor.GREEN + String.valueOf(value);
            }

            line += value;

            // Trim
            String lastColor = "";
            while (line.length() > lineLimit) {
                String part = lastColor + line.substring(0, lineLimit + 1);
                line = line.substring(lineLimit + 1);
                description.add(part);
                lastColor = ChatColor.getLastColors(part);
            }

            if (!line.isEmpty()) {
                description.add(lastColor + line);
            }
        }

        setDescription(description);
    }

    @Override
    public @Nullable ItemStack onClick() {
        if (act.displayMenu(getContainer().getViewer(), getContainer())) {
            return null;
        }
        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        if (exec != null)
            exec.removeAction(act);
        getContainer().removeItem(getSlot());
        return null;
    }
}
