package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MenuItemEnum<T extends Enum<T>> extends MenuItem {
    private final static String DESCRIPTION_VALUE_TOKEN = "EnumValue_description";
    private final @NotNull List<T> enumList;
    private final @NotNull Callback<T> callback;

    public MenuItemEnum(@Nullable Material displayMat, @Nullable Component name,
                        @Nullable List<@NotNull Component> description, @NotNull Callback<T> callback,
                        @NotNull Class<T> enumClass) {
        super(displayMat, name, description);
        this.callback = callback;
        enumList = new ArrayList<>(EnumSet.allOf(enumClass));
        updateDescription();
    }

    public MenuItemEnum(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<T> callback,
                        @NotNull Class<T> enumClass) {
        super(displayMat, name);
        this.callback = callback;
        enumList = new ArrayList<>(EnumSet.allOf(enumClass));
        updateDescription();
    }

    protected final void updateDescription() {
        if (enumList.isEmpty()) {
            return;
        }

        int position = enumList.indexOf(callback.getValue());
        if (position == -1) {
            setDescriptionPartAtEnd(DESCRIPTION_VALUE_TOKEN,
                    List.of(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_ENUM_ERROR)));
        }

        int last = position - 1;
        int next = position + 1;
        if (last < 0) {
            last = enumList.size() - 1;
        }
        if (next >= enumList.size()) {
            next = 0;
        }

        List<Component> options = new ArrayList<>(3);
        options.add(Component.text(getEnumName(enumList.get(last)), NamedTextColor.GRAY));
        options.add(Component.text(getEnumName(enumList.get(position)), NamedTextColor.GREEN));
        options.add(Component.text(getEnumName(enumList.get(next)), NamedTextColor.GRAY));

        setDescriptionPartAtEnd(DESCRIPTION_VALUE_TOKEN, options);
    }

    private String getEnumName(T val) {
        return WordUtils.capitalizeFully(val.name().replace('_', ' '));
    }

    public final ItemStack onClick() {
        T oldValue = callback.getValue();
        T newValue = increaseValue(oldValue, false);
        callback.setValue(newValue);

        updateDescription();

        return getDisplayItem();
    }

    @Override
    public final ItemStack onShiftClick() {
        T oldValue = callback.getValue();
        T newValue = increaseValue(oldValue, true);
        callback.setValue(newValue);

        updateDescription();

        return getDisplayItem();
    }

    @Override
    public final ItemStack onRightClick() {
        T oldValue = callback.getValue();
        T newValue = decreaseValue(oldValue, false);
        callback.setValue(newValue);

        updateDescription();

        return getDisplayItem();
    }

    @Override
    public final ItemStack onShiftRightClick() {
        T oldValue = callback.getValue();
        T newValue = decreaseValue(oldValue, true);
        callback.setValue(newValue);

        updateDescription();

        return getDisplayItem();
    }

    protected T increaseValue(T current, boolean shift) {
        if (enumList.isEmpty()) {
            return null;
        }

        int index = enumList.indexOf(current);
        if (index == -1) {
            return enumList.get(0);
        }

        ++index;
        if (index >= enumList.size()) {
            index = 0;
        }

        return enumList.get(index);
    }

    protected T decreaseValue(T current, boolean shift) {
        if (enumList.isEmpty()) {
            return null;
        }

        int index = enumList.indexOf(current);
        if (index == -1) {
            return enumList.get(0);
        }

        --index;
        if (index < 0) {
            index = enumList.size() - 1;
        }

        return enumList.get(index);
    }
}
