package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemCustom extends MenuItem {
    private InteractionInterface click = null;
    private InteractionInterface clickItem = null;
    private InteractionInterface rightClick = null;
    private InteractionInterface shiftClick = null;
    private InteractionInterface shiftRightClick = null;
    private InteractionInterface doubleClick = null;

    public MenuItemCustom(@Nullable Material displayMat, @Nullable Component name) {
        super(displayMat, name);
    }

    public MenuItemCustom(@Nullable Material displayMat, @Nullable Component name,
                          @Nullable List<@NotNull Component> description) {
        super(displayMat, name, description);
    }

    @Override
    public ItemStack onClick() {
        if (click != null)
            return (ItemStack) click.interact(null);
        return getDisplayItem();
    }

    public void setClick(InteractionInterface ii) {
        click = ii;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        if (clickItem != null)
            return (ItemStack) clickItem.interact(item);
        return getDisplayItem();
    }

    public void setClickItem(InteractionInterface ii) {
        clickItem = ii;
    }

    @Override
    public ItemStack onRightClick() {
        if (rightClick != null)
            return (ItemStack) rightClick.interact(null);
        return getDisplayItem();
    }

    public void setRightClick(InteractionInterface ii) {
        rightClick = ii;
    }

    @Override
    public ItemStack onShiftClick() {
        if (shiftClick != null)
            return (ItemStack) shiftClick.interact(null);
        return getDisplayItem();
    }

    public void setShiftClick(InteractionInterface ii) {
        shiftClick = ii;
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (shiftRightClick != null)
            return (ItemStack) shiftRightClick.interact(null);
        return getDisplayItem();
    }

    public void setShiftRightClick(InteractionInterface ii) {
        shiftRightClick = ii;
    }

    @Override
    public ItemStack onDoubleClick() {
        if (doubleClick != null)
            return (ItemStack) doubleClick.interact(null);
        return getDisplayItem();
    }

    public void setDoubleClick(InteractionInterface ii) {
        doubleClick = ii;
    }
}
