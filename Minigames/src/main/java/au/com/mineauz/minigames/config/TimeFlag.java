package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItemTime;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimeFlag extends Flag<Long> {

    public TimeFlag(Long value, String name) {
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
        setFlag(config.getLong(path + "." + getName()));
    }

    @Deprecated
    @Override
    public MenuItemTime getMenuItem(@Nullable Component name, @Nullable Material displayMat) {
        return getMenuItem(name, displayMat, null);
    }

    public MenuItemTime getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                    @Nullable Long min, @Nullable Long max) {
        return getMenuItem(name, displayMat, null, min, max);
    }

    @Deprecated
    @Override
    public MenuItemTime getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                    @Nullable List<@NotNull Component> description) {
        return getMenuItem(name, displayMat, description, 0L, null);
    }

    public MenuItemTime getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                    @Nullable List<@NotNull Component> description, @Nullable Long min, @Nullable Long max) {
        return new MenuItemTime(displayMat, name, description, new Callback<>() {

            @Override
            public Long getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Long value) {
                setFlag(value);
            }

        }, min, max);
    }
}
