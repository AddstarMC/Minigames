package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnumFlag<T extends Enum<T>> extends Flag<T> {
    private final Class<T> enumClass;

    @SuppressWarnings("unchecked")
    public EnumFlag(T value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
        enumClass = (Class<T>) value.getClass();
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().name());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        setFlag(T.valueOf(enumClass, config.getString(path + "." + getName())));
    }

    /**
     * @param description will get ignored
     */
    @Override
    public MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                @Nullable List<@NotNull Component> description) {
        return new MenuItemEnum<>(displayMat, name, new Callback<>() {

            @Override
            public T getValue() {
                return getFlag();
            }

            @Override
            public void setValue(T value) {
                setFlag(value);
            }
        }, enumClass);
    }
}
