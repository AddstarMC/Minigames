package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MenuItemTime extends MenuItemInteger {

    public MenuItemTime(String name, Material displayItem, Callback<Integer> value, Integer min, Integer max) {
        super(name, displayItem, value, min, max);
    }

    public MenuItemTime(String name, List<String> description, Material displayItem, Callback<Integer> value, Integer min, Integer max) {
        super(name, description, displayItem, value, min, max);
    }

    @Override
    public void updateDescription() {
        List<String> description = null;
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("([0-9]+[dhms]:?)+"))
                description.set(0, ChatColor.GREEN.toString() + MinigameUtils.convertTime(getValue().getValue(), true));
            else
                description.add(0, ChatColor.GREEN.toString() + MinigameUtils.convertTime(getValue().getValue(), true));
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN.toString() + MinigameUtils.convertTime(getValue().getValue(), true));
        }

        setDescription(description);
    }
}
