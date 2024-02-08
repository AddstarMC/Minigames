package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntegerFlag extends Flag<Integer> {

    public IntegerFlag(Integer value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        setFlag(config.getInt(path + "." + getName()));
    }

    @Deprecated
    @Override
    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name) {
        return getMenuItem(displayMat, name, 0, null);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable Integer min, @Nullable Integer max) {
        return getMenuItem(displayMat, name, null, min, max);
    }

    @Deprecated
    @Override
    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description) {
        return getMenuItem(displayMat, name, description, 0, null);
    }

    public MenuItemInteger getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                       @Nullable List<@NotNull Component> description, @Nullable Integer min, @Nullable Integer max) {
        return new MenuItemInteger(displayMat, name, description, new Callback<>() {

            @Override
            public Integer getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Integer value) {
                setFlag(value);
            }

        }, min, max);
    }
}
