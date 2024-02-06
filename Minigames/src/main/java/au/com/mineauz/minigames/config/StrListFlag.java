package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StrListFlag extends Flag<List<String>> {

    public StrListFlag(List<String> value, String name) {
        setFlag(value);
        setDefaultFlag(new ArrayList<>()); //saving tests if the flag is equal to their default
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        setFlag(config.getStringList(path + "." + getName()));
    }

    @Override
    public MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayItem) {
        return getMenuItem(name, displayItem, null);
    }

    @Override
    public MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayItem,
                                @Nullable List<@NotNull Component> description) {
        return null; //todo
    }
}
