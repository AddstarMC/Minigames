package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleLocationFlag extends Flag<Location> {

    public SimpleLocationFlag(Location value, String name) {
        setFlag(value);
        setDefaultFlag(value);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName() + ".x", getFlag().getX());
        config.set(path + "." + getName() + ".y", getFlag().getY());
        config.set(path + "." + getName() + ".z", getFlag().getZ());
        config.set(path + "." + getName() + ".world", getFlag().getWorld().getName());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        double x = config.getDouble(path + "." + getName() + ".x");
        double y = config.getDouble(path + "." + getName() + ".y");
        double z = config.getDouble(path + "." + getName() + ".z");
        String world = config.getString(path + "." + getName() + ".world");

        setFlag(new Location(Bukkit.getWorld(world), x, y, z));
    }

    @Deprecated
    @Override
    public MenuItem getMenuItem(@Nullable Material displayMat, @Nullable Component name) {
        return getMenuItem(displayMat, name, null);
    }

    @Deprecated
    @Override
    public MenuItem getMenuItem(@Nullable Material displayMat, @Nullable Component name,
                                @Nullable List<@NotNull Component> description) {
        return null;
    }

}
