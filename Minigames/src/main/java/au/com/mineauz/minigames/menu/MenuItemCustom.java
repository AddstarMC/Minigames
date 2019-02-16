package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuItemCustom extends MenuItem {

    private InteractionInterface click = null;
    private InteractionInterface clickItem = null;
    private InteractionInterface rightClick = null;
    private InteractionInterface shiftClick = null;
    private InteractionInterface shiftRightClick = null;
    private InteractionInterface doubleClick = null;

    public MenuItemCustom(String name, Material displayItem) {
        super(name, displayItem);
    }

    public MenuItemCustom(String name, List<String> description, Material displayItem) {
        super(name, description, displayItem);
    }

    @Override
    public ItemStack onClick() {
        if (click != null)
            return (ItemStack) click.interact(null);
        return getItem();
    }

    public void setClick(InteractionInterface ii) {
        click = ii;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        if (clickItem != null)
            return (ItemStack) clickItem.interact(item);
        return getItem();
    }

    public void setClickItem(InteractionInterface ii) {
        clickItem = ii;
    }

    @Override
    public ItemStack onRightClick() {
        if (rightClick != null)
            return (ItemStack) rightClick.interact(null);
        return getItem();
    }

    public void setRightClick(InteractionInterface ii) {
        rightClick = ii;
    }

    @Override
    public ItemStack onShiftClick() {
        if (shiftClick != null)
            return (ItemStack) shiftClick.interact(null);
        return getItem();
    }

    public void setShiftClick(InteractionInterface ii) {
        shiftClick = ii;
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (shiftRightClick != null)
            return (ItemStack) shiftRightClick.interact(null);
        return getItem();
    }

    public void setShiftRightClick(InteractionInterface ii) {
        shiftRightClick = ii;
    }

    @Override
    public ItemStack onDoubleClick() {
        if (doubleClick != null)
            return (ItemStack) doubleClick.interact(null);
        return getItem();
    }

    public void setDoubleClick(InteractionInterface ii) {
        doubleClick = ii;
    }
}
