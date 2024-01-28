package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MenuItemTime extends MenuItemInteger {
    public MenuItemTime(Component name, Material displayItem, Callback<Integer> value, Integer min, Integer max) {
        super(name, displayItem, value, min, max);
    }

    public MenuItemTime(Component name, List<Component> description, Material displayItem, Callback<Long> value, Integer min, Integer max) {
        super(name, description, displayItem, value, min, max);
    }

    @Override
    public void updateDescription() {
        List<Component> description;
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("([0-9]+[dhms]:?)+"))
                description.set(0, ChatColor.GREEN + MinigameUtils.convertTime(getValue().getValue(), true));
            else
                description.add(0, ChatColor.GREEN + MinigameUtils.convertTime(getValue().getValue(), true));
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN + MinigameUtils.convertTime(getValue().getValue(), true));
        }

        setDescription(description);
    }
}
