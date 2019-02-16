package au.com.mineauz.minigames.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Used when the menu item holds a material.
 * <p>
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 15/11/2018.
 */
public class MenuItemMaterial extends MenuItem {

    private Callback<Material> materialCallback;

    public MenuItemMaterial(String name, Material displayItem) {
        super(name, displayItem);
    }

    public MenuItemMaterial(String name, Material displayItem, Callback<Material> c) {
        super(name, displayItem);
        materialCallback = c;
    }

    public MenuItemMaterial(String name, List<String> description, Material displayItem, Callback<Material> c) {
        super(name, description, displayItem);
        materialCallback = c;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        materialCallback.setValue(item.getType());
        updateDescription();
        return super.onClickWithItem(item);
    }

    @Override
    public ItemStack onShiftRightClick() {
        materialCallback.setValue(Material.STONE);
        return super.onShiftRightClick();
    }

    private List<String> createDescription(Material data) {
        List<String> result = new ArrayList<>();
        result.add("Material: " + data.name());
        return result;
    }

    public void updateDescription() {
        List<String> description;
        Material setting = materialCallback.getValue();

        if (getDescription() != null) {
            description = getDescription();
            String desc = getDescription().get(0);

            if (desc.startsWith(ChatColor.GREEN.toString()))
                description.set(0, ChatColor.GREEN.toString() + createDescription(setting));
            else
                description.add(0, ChatColor.GREEN.toString() + createDescription(setting));
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN.toString() + createDescription(setting));
        }
        setDescription(description);
        setItem(new ItemStack(materialCallback.getValue(), 1));
    }


}
