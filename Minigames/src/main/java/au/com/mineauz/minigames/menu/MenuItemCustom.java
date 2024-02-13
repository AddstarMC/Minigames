package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MenuItemCustom extends MenuItem {
    private Supplier<@Nullable ItemStack> click = null;
    private Function<ItemStack, @Nullable ItemStack> clickItem = null;
    private Supplier<@Nullable ItemStack> rightClick = null;
    private Supplier<@Nullable ItemStack> shiftClick = null;
    private Supplier<@Nullable ItemStack> shiftRightClick = null;
    private Supplier<@Nullable ItemStack> doubleClick = null;

    public MenuItemCustom(@Nullable Material displayMat, @Nullable Component name) {
        super(displayMat, name);
    }

    public MenuItemCustom(@Nullable Material displayMat, @NotNull LangKey langKey) {
        super(displayMat, langKey);
    }

    public MenuItemCustom(@Nullable Material displayMat, @NotNull LangKey langKey,
                          @Nullable List<@NotNull Component> description) {
        super(displayMat, langKey, description);
    }

    public MenuItemCustom(@Nullable Material displayMat, @Nullable Component name,
                          @Nullable List<@NotNull Component> description) {
        super(displayMat, name, description);
    }

    @Override
    public ItemStack onClick() {
        if (click != null) {
            return click.get();
        }
        return getItem();
    }

    public void setClick(Supplier<@Nullable ItemStack> sup) {
        click = sup;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        if (clickItem != null)
            return clickItem.apply(item);
        return getItem();
    }

    public void setClickItem(Function<ItemStack, @Nullable ItemStack> func) {
        clickItem = func;
    }

    @Override
    public ItemStack onRightClick() {
        if (rightClick != null) {
            return rightClick.get();
        }
        return getItem();
    }

    public void setRightClick(Supplier<@Nullable ItemStack> sup) {
        rightClick = sup;
    }

    @Override
    public ItemStack onShiftClick() {
        if (shiftClick != null) {
            return shiftClick.get();
        }
        return getItem();
    }

    public void setShiftClick(Supplier<@Nullable ItemStack> sup) {
        shiftClick = sup;
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (shiftRightClick != null) {
            return shiftRightClick.get();
        }
        return getItem();
    }

    public void setShiftRightClick(Supplier<@Nullable ItemStack> sup) {
        shiftRightClick = sup;
    }

    @Override
    public ItemStack onDoubleClick() {
        if (doubleClick != null) {
            return doubleClick.get();
        }
        return getItem();
    }

    public void setDoubleClick(Supplier<@Nullable ItemStack> sup) {
        doubleClick = sup;
    }
}
