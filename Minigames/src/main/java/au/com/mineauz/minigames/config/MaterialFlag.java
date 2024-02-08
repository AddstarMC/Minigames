package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 15/11/2018.
 */
public class MaterialFlag extends Flag<Material> {
    public MaterialFlag(Material mat, String name) {
        setFlag(mat);
        setDefaultFlag(mat);
        setName(name);
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().name());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        if (config.contains(path + "." + getName())) {
            Material flag = Material.matchMaterial(config.getString(path + "." + getName()));
            if (flag == null) {
                flag = Material.STONE;
                Minigames.getCmpnntLogger().warn("Failed to load Material from config at :" + path + "." + getName() + " Value: " + config.getString(path + "." + getName()));
            }
            setFlag(flag);
        } else {
            setFlag(getDefaultFlag());
        }
    }

    public MenuItem getMenuItem(@Nullable Component name) {
        return getMenuItem(getFlag(), name, null);
    }

    public MenuItem getMenuItem(@Nullable Component name, @Nullable List<@NotNull Component> description) {
        return getMenuItem(getFlag(), name, description);
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
        return new MenuItemMaterial(displayMat, name, description, new Callback<>() {
            @Override
            public Material getValue() {
                return getFlag();
            }

            @Override
            public void setValue(Material value) {
                setFlag(value);
            }
        });
    }
}
