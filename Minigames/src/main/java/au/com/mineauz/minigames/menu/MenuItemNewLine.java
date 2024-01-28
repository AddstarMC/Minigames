package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuItemNewLine extends MenuItem {

    public MenuItemNewLine() {
        super(Component.text("NL"), (Material) null);
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
