package au.com.mineauz.minigames.menu;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;

public class MenuItemEnum<T extends Enum<T>> extends MenuItem {
    private final List<String> baseDescription;
    private final List<T> enumList;
    private final Callback<T> callback;

    public MenuItemEnum(String name, List<String> description, Material displayItem, Callback<T> callback, Class<T> enumClass) {
        super(name, description, displayItem);
        this.callback = callback;
        enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
        baseDescription = description;
        updateDescription();
    }

    public MenuItemEnum(String name, Material displayItem, Callback<T> callback, Class<T> enumClass) {
        super(name, displayItem);
        this.callback = callback;
        enumList = Lists.newArrayList(EnumSet.allOf(enumClass));
        baseDescription = Collections.emptyList();
        updateDescription();
    }

    protected final void updateDescription() {
        List<String> valueDesc = getValueDescription(callback.getValue());
        super.setDescription(Lists.newArrayList(Iterators.concat(valueDesc.iterator(), baseDescription.iterator())));
    }

    protected List<String> getValueDescription(T value) {
        // For the initial update
        if (enumList == null) {
            return Collections.emptyList();
        }

        if (enumList.isEmpty()) {
            return Collections.emptyList();
        }

        int position = enumList.indexOf(value);
        if (position == -1) {
            return Collections.singletonList(ChatColor.RED + "*ERROR*");
        }

        int last = position - 1;
        int next = position + 1;
        if (last < 0) {
            last = enumList.size() - 1;
        }
        if (next >= enumList.size()) {
            next = 0;
        }

        List<String> options = Lists.newArrayListWithCapacity(3);
        options.add(ChatColor.GRAY + getEnumName(enumList.get(last)));
        options.add(ChatColor.GREEN + getEnumName(enumList.get(position)));
        options.add(ChatColor.GRAY + getEnumName(enumList.get(next)));

        return options;
    }

    private String getEnumName(T val) {
        return WordUtils.capitalizeFully(val.name().replace('_', ' '));
    }

    public final ItemStack onClick() {
        T oldValue = callback.getValue();
        T newValue = increaseValue(oldValue, false);
        callback.setValue(newValue);

        updateDescription();

        return getItem();
    }

    @Override
    public final ItemStack onShiftClick() {
        T oldValue = callback.getValue();
        T newValue = increaseValue(oldValue, true);
        callback.setValue(newValue);

        updateDescription();

        return getItem();
    }

    @Override
    public final ItemStack onRightClick() {
        T oldValue = callback.getValue();
        T newValue = decreaseValue(oldValue, false);
        callback.setValue(newValue);

        updateDescription();

        return getItem();
    }

    @Override
    public final ItemStack onShiftRightClick() {
        T oldValue = callback.getValue();
        T newValue = decreaseValue(oldValue, true);
        callback.setValue(newValue);

        updateDescription();

        return getItem();
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
