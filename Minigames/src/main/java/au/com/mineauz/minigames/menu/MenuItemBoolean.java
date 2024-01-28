package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemBoolean extends MenuItem {
    private final Callback<Boolean> toggle;

    public MenuItemBoolean(Component name, Material displayItem, Callback<Boolean> toggle) {
        super(name, displayItem);
        this.toggle = toggle;
        updateDescription();
    }

    public MenuItemBoolean(Component name, List<Component> description, Material displayItem, Callback<Boolean> toggle) {
        super(name, description, displayItem);
        this.toggle = toggle;
        updateDescription();
    }

    public void updateDescription() {
        List<Component> description;
        String col;
        if (toggle.getValue()) {
            col = ChatColor.GREEN + "true";
        } else {
            col = ChatColor.RED + "false";
        }
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("true|false"))
                description.set(0, col);
            else
                description.add(0, col);
        } else {
            description = new ArrayList<>();
            description.add(col);
        }

        setDescriptionStr(description);
    }

    @Override
    public ItemStack onClick() {
        if (toggle.getValue())
            toggle.setValue(false);
        else
            toggle.setValue(true);

        updateDescription();
        return getDisplayItem();
    }
}
