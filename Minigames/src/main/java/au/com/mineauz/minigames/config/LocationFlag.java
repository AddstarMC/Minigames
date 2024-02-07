package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.menu.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocationFlag extends Flag<Location> {

    public LocationFlag(Location value, String name) {
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
        config.set(path + "." + getName() + ".yaw", getFlag().getYaw());
        config.set(path + "." + getName() + ".pitch", getFlag().getPitch());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        double x = config.getDouble(path + "." + getName() + ".x");
        double y = config.getDouble(path + "." + getName() + ".y");
        double z = config.getDouble(path + "." + getName() + ".z");
        float yaw = ((Double) config.getDouble(path + "." + getName() + ".yaw")).floatValue();
        float pitch = ((Double) config.getDouble(path + "." + getName() + ".pitch")).floatValue();
        String world = config.getString(path + "." + getName() + ".world");

        setFlag(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat) {
        return getMenuItem(name, displayMat, null);
    }

    @Deprecated
    @Override
    public @Nullable MenuItem getMenuItem(@Nullable Component name, @Nullable Material displayMat,
                                          @Nullable List<@Nullable Component> description) {
        return null;
    }
}
