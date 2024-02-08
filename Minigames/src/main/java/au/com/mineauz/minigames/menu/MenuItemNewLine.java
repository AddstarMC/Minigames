package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class MenuItemNewLine extends MenuItem {

    public MenuItemNewLine() {
        super(null, Component.text("NL")); // since it will never be visible anyway we can hardcode the name
    }

    @Override
    public ItemStack onClick() {
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        return null;
    }

    @Override
    public ItemStack onShiftClick() {
        return null;
    }

    @Override
    public ItemStack onShiftRightClick() {
        return null;
    }

    @Override
    public ItemStack onDoubleClick() {
        return null;
    }
}
