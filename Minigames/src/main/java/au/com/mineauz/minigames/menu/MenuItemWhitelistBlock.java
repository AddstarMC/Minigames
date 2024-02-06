package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemWhitelistBlock extends MenuItem {
    private final List<Material> whitelist;

    public MenuItemWhitelistBlock(@NotNull Material displayItem, List<Material> whitelist) {
        super(Component.translatable(displayItem.translationKey()), displayItem);
        setDescription(List.of("Right Click to remove"));
        this.whitelist = whitelist;
    }

    @Override
    public ItemStack onRightClick() {
        whitelist.remove(getItem().getType());
        getContainer().removeItem(getSlot());
        return null;
    }
}
